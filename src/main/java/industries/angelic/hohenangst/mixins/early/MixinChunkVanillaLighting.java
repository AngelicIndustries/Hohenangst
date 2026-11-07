package industries.angelic.hohenangst.mixins.early;

import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/** Vanilla's relighting; not loaded with Supernova, which replaces these methods with its own height-aware engine. */
@Mixin(value = Chunk.class, priority = 1100)
public abstract class MixinChunkVanillaLighting {

    @ModifyConstant(method = "enqueueRelightChecks", constant = @Constant(intValue = 4096), require = 1)
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
}
