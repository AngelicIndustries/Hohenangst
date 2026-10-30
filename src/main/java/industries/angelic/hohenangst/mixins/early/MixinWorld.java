package industries.angelic.hohenangst.mixins.early;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.world.World", priority = 1100)
public abstract class MixinWorld {

    @ModifyConstant(
        method = { "getBlock", "blockExists", "checkChunksExist", "setBlock", "getBlockMetadata",
            "setBlockMetadataWithNotify", "getFullBlockLightValue", "getBlockLightValue_do",
            "getSkyBlockTypeBrightness", "getSavedLightValue", "setLightValue", "getTileEntity" },
        constant = @Constant(intValue = 256),
        require = 1)
    private int hohenangst$constant0(int original) {
        return 512;
    }

    @ModifyConstant(
        method = { "getFullBlockLightValue", "getBlockLightValue_do", "getSavedLightValue" },
        constant = @Constant(intValue = 255),
        require = 1)
    private int hohenangst$constant1(int original) {
        return 511;
    }

    @ModifyConstant(
        method = { "getBlockLightOpacity", "canBlockFreezeBody", "canSnowAtBody" },
        constant = @Constant(intValue = 256),
        remap = false,
        require = 1)
    private int hohenangst$forgeOpacity(int original) {
        return 512;
    }
}
