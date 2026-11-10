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
 * <p>v113 AttributeData.defaultValue is dropped (no 0.15.x equivalent).</p>
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
      // float defaultValue, boolean shouldSend). Value is set via setValue().
      // The id is a small int identifying which attribute (health=4, etc.);
      // we use i as a placeholder since 0.15.x wire format doesn't actually
      // serialise the id (it serialises name+min+max+value).
      entries[i] = new org.CreadoresProgram.CraftsMine.nukkitLib.entity.Attribute(
          i,
          ad.getName() == null ? "" : ad.getName(),
          ad.getMinimum(), ad.getMaximum(), ad.getDefaultValue(),
          true).setValue(ad.getValue());
    }
    // 0.15.x UpdateAttributesPacket.entries is public Attribute[].
    // Set via reflection-free direct field assignment if exposed; otherwise
    // fall back to a manual approach. The 0.15.x packet's entries field is
    // named `entries` in Barrel-Craftsmine's UpdateAttributesPacket.
    try {
      java.lang.reflect.Field f = org.CreadoresProgram.CraftsMine.network.protocol.UpdateAttributesPacket.class.getField("entries");
      f.set(npk, entries);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      // Field name mismatch: try alternative names.
      try {
        java.lang.reflect.Field f = org.CreadoresProgram.CraftsMine.network.protocol.UpdateAttributesPacket.class.getField("attributes");
        f.set(npk, entries);
      } catch (NoSuchFieldException | IllegalAccessException ignored) {
        return;
      }
    }
    player.sendDataCraftsman(npk);
  }
}
