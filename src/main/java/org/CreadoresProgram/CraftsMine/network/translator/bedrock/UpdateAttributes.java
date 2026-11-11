package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.UpdateAttributesPacket;
import com.nukkitx.protocol.bedrock.data.AttributeData;
import org.CreadoresProgram.CraftsMine.nukkitLib.entity.Attribute;

/**
 * v113 UpdateAttributesPacket (runtimeEntityId, List&lt;AttributeData&gt;) →
 * 0.15.x UpdateAttributesPacket (entityId, Attribute[] entries).
 *
 * <p>v113 AttributeData: name, minimum, maximum, value, defaultValue.
 * 0.15.x Attribute exposes min/max/value via getters and a name field.
 * The 0.15.x wire format is `count short + per attr: float(min), float(max),
 * float(value), String(name)` (note name is LAST).</p>
 *
 * <p>v113 AttributeData.defaultValue is dropped (no 0.15.x equivalent in
 * the wire format; we still pass it to the Attribute constructor because
 * the proxy's Attribute class uses it to initialise currentValue before
 * setValue is called).</p>
 *
 * <p>CRITICAL: the proxy's {@code Attribute.setValue(float)} (single-arg
 * overload) throws {@code IllegalArgumentException("Value X exceeds the
 * range!")}$ when value is outside [min, max]. The v113 downstream server
 * routinely sends out-of-range current values for players with absorption
 * (health > max_health), modifier-buffed movement speed, or hunger
 * saturation > 20 from plugins. The crash previously surfaced as
 * "Datapacket translate error (UpdateAttributesPacket)" with the player
 * getting disconnected. The fix uses {@code setValue(value, true)} which
 * clamps the value to [min, max] instead of throwing.</p>
 */
public class UpdateAttributes implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    UpdateAttributesPacket packet = (UpdateAttributesPacket) pk;
    java.util.List<AttributeData> attrs = packet.getAttributes();
    if (attrs == null || attrs.isEmpty()) return;
    org.CreadoresProgram.CraftsMine.network.protocol.UpdateAttributesPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.UpdateAttributesPacket();
    npk.entityId = packet.getRuntimeEntityId();
    org.CreadoresProgram.CraftsMine.nukkitLib.entity.Attribute[] entries =
        new org.CreadoresProgram.CraftsMine.nukkitLib.entity.Attribute[attrs.size()];
    for (int i = 0; i < attrs.size(); i++) {
      AttributeData ad = attrs.get(i);
      // Attribute constructor: (int id, String name, float min, float max,
      // float defaultValue, boolean shouldSend). The id is never serialised
      // on the 0.15.x wire (only name+min+max+value are), so a positional
      // index `i` is a safe placeholder.
      String name = ad.getName() == null ? "" : ad.getName();
      Attribute attr = new org.CreadoresProgram.CraftsMine.nukkitLib.entity.Attribute(
          i,
          name,
          ad.getMinimum(),
          ad.getMaximum(),
          ad.getDefaultValue(),
          true);
      // CRITICAL: setValue(value, fit=true) clamps the value into [min, max]
      // instead of throwing IllegalArgumentException. The v113 server can
      // legitimately send values outside the legacy range (absorption
      // health, plugin-applied modifiers, etc); the previous call to
      // setValue(value) (single-arg, fit=false) crashed on every such
      // packet, disconnecting the player whenever an attribute was out of
      // range.
      float clampedValue = Math.min(Math.max(ad.getValue(), ad.getMinimum()), ad.getMaximum());
      attr.setValue(clampedValue, true);
      entries[i] = attr;
    }
    // The proxy's UpdateAttributesPacket.entries field is `public
    // Attribute[] entries;` (see UpdateAttributesPacket.java line 14).
    // Direct field assignment replaces the previous fragile reflection
    // lookup that probed both "entries" and "attributes" field names.
    npk.entries = entries;
    player.sendDataCraftsman(npk);
  }
}
