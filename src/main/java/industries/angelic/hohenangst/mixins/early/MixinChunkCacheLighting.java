package industries.angelic.hohenangst.mixins.early;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/** Forge removes these rendering methods from physical dedicated servers. */
@Mixin(targets = "net.minecraft.world.ChunkCache", priority = 1100)
public abstract class MixinChunkCacheLighting {

    @ModifyConstant(
        method = { "getSkyBlockTypeBrightness", "getSpecialBlockBrightness", "getHeight" },
        constant = @Constant(intValue = 256),
        require = 1)
    private int hohenangst$height(int original) {
        return 512;
    }

    @ModifyConstant(
        method = { "getSkyBlockTypeBrightness", "getSpecialBlockBrightness" },
        constant = @Constant(intValue = 255),
        require = 1)
    private int hohenangst$ceiling(int original) {
        return 511;
    }
}
