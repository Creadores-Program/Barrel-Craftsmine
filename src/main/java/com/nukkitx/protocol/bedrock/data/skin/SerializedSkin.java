package com.nukkitx.protocol.bedrock.data.skin;

import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.JSONValue;
import lombok.*;

import static com.nukkitx.network.util.Preconditions.checkArgument;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SerializedSkin {
    private static final int PIXEL_SIZE = 4;

    public static final int SINGLE_SKIN_SIZE = 64 * 32 * PIXEL_SIZE;
    public static final int DOUBLE_SKIN_SIZE = 64 * 64 * PIXEL_SIZE;

    private final String skinId;

    private final ImageData skinData;

    public static SerializedSkin of(String skinId, ImageData skinData) {
        skinData.checkLegacySkinSize();

        return new SerializedSkin(skinId, skinData);
    }

    public static Builder builder() {
        return new Builder();
    }

    private boolean isValidSkin() {
        return skinId != null && !skinId.trim().isEmpty() &&
                skinData != null && skinData.getWidth() >= 64 && skinData.getHeight() >= 32 &&
                skinData.getImage().length >= SINGLE_SKIN_SIZE;
    }

    private static String convertLegacyGeometryName(String geometryName) {
        return "{\"geometry\" : {\"default\" : \"" + JSONValue.escape(geometryName) + "\"}}";
    }

    private static String convertSkinPatchToLegacy(String skinResourcePatch) {
        checkArgument(validateSkinResourcePatch(skinResourcePatch), "Invalid skin resource patch");
        JSONObject object = (JSONObject) JSONValue.parse(skinResourcePatch);
        JSONObject geometry = (JSONObject) object.get("geometry");
        return (String) geometry.get("default");
    }

    private static boolean validateSkinResourcePatch(String skinResourcePatch) {
        try {
            JSONObject object = (JSONObject) JSONValue.parse(skinResourcePatch);
            JSONObject geometry = (JSONObject) object.get("geometry");
            return geometry.containsKey("default") && geometry.get("default") instanceof String;
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    public Builder toBuilder() {
        return new Builder().skinId(this.skinId).skinData(this.skinData);
    }

    public static class Builder {
        private String skinId;
        private ImageData skinData;

        Builder() {
        }

        public Builder skinId(String skinId) {
            this.skinId = skinId;
            return this;
        }

        public Builder skinData(ImageData skinData) {
            this.skinData = skinData;
            return this;
        }

        public SerializedSkin build() {
            return SerializedSkin.of(skinId, skinData);
        }

        public String toString() {
            return "SerializedSkin.Builder(skinId=" + this.skinId +
                    ", skinData=" + this.skinData +
                    ")";
        }
    }
}
