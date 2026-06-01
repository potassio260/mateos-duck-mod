package net.mateo.robomod.util;

import net.minecraft.client.model.geom.ModelPart;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.Optional;

/**
 * Fabric (Yarn) → Forge (Official/MCP) ModelPart field renames:
 *   pivotX / pivotY / pivotZ  →  x / y / z
 *   pitch / yaw / roll        →  xRot / yRot / zRot
 *
 * GeckoLib's GeoBone API is identical between Fabric and Forge builds.
 */
public class RenderUtils {

    public static void setPositionGeoBone(
            Optional<GeoBone> bone,
            float x, float y, float z,
            float originalY, float sneakingOffset,
            ModelPart referenceModel,
            Optional<GeoBone> bone_local) {

        setPositionGeoBone(bone, x, y, z, originalY, sneakingOffset, referenceModel, 0, 0, 0, bone_local);
    }

    public static void setPositionGeoBone(
            Optional<GeoBone> bone,
            float x, float y, float z,
            float originalY, float sneakingOffset,
            ModelPart referenceModel,
            float xr, float yr, float zr,
            Optional<GeoBone> bone_local) {

        if (bone.isPresent()) {
            // pivotX → x,  pivotZ → z
            bone.get().updatePosition(
                    8f  - (referenceModel.x),
                    16f - (originalY - sneakingOffset),
                    -8  - (-referenceModel.z));
            // pitch → xRot,  yaw → yRot,  roll → zRot
            bone.get().updateRotation(referenceModel.xRot, referenceModel.yRot, referenceModel.zRot);
        }

        if (bone_local.isPresent() && bone != bone_local) {
            bone_local.get().updatePosition(x, y, z);
            bone_local.get().updateRotation(
                    (float) Math.toRadians(xr),
                    (float) Math.toRadians(yr),
                    (float) Math.toRadians(zr));
        }
    }

    public static void setPositionGeoBoneAssembler(
            Optional<GeoBone> bone,
            float x, float y, float z,
            float pitch, float yaw, float roll) {

        if (bone.isPresent()) {
            bone.get().updatePosition(8f - x, y - 8, -8 - (-z));
            bone.get().updateRotation(
                    (float) Math.toRadians(pitch),
                    (float) Math.toRadians(yaw),
                    (float) Math.toRadians(roll));
        }
    }
}
