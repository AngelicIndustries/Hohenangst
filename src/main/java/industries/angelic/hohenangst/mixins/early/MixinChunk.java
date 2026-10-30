package industries.angelic.hohenangst.mixins.early;

import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import industries.angelic.hohenangst.world.HeightRange;

@Mixin(value = Chunk.class, priority = 1100)
public abstract class MixinChunk {

    // Slot 4 is the opacity-scan cursor; only its initial store, never the decrement.
    @ModifyVariable(method = "generateSkylightMap", at = @At(value = "STORE", ordinal = 0), index = 4, require = 1)
    private int hohenangst$includeUpperSectionTop(int original) {
        return HeightRange.rebuildScanStart(original);
    }

    @ModifyConstant(method = "<init>(Lnet/minecraft/world/World;II)V", constant = @Constant(intValue = 16), require = 2)
    private int hohenangst$sectionArrays(int original) {
        return 32;
    }

    @ModifyConstant(
        method = { "<init>(Lnet/minecraft/world/World;II)V", "enqueueRelightChecks" },
        constant = @Constant(intValue = 4096),
        require = 2)
    private int hohenangst$relightCount(int original) {
        return 8192;
    }

    @ModifyConstant(
        method = "enqueueRelightChecks",
        constant = { @Constant(intValue = 16, ordinal = 0), @Constant(intValue = 16, ordinal = 1) },
        require = 2)
    private int hohenangst$relightStride(int original) {
        return 32;
    }

    @ModifyConstant(method = "enqueueRelightChecks", constant = @Constant(intValue = 256), require = 1)
    private int hohenangst$relightPlane(int original) {
        return 512;
    }

    // Heightmap entries may be 512 (first air above the top block), so a 9-bit mask is also wrong.
    @ModifyConstant(method = "relightBlock", constant = @Constant(intValue = 255), require = 1)
    private int hohenangst$unmaskedHeightmap(int original) {
        return -1;
    }

    @ModifyConstant(method = "getAreLevelsEmpty", constant = @Constant(intValue = 256), require = 1)
    private int hohenangst$emptyLimit(int original) {
        return 512;
    }

    @ModifyConstant(method = "getAreLevelsEmpty", constant = @Constant(intValue = 255), require = 1)
    private int hohenangst$emptyTop(int original) {
        return 511;
    }
}
