package industries.angelic.hohenangst.mixins.early;

import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import industries.angelic.hohenangst.world.HeightRange;

/** Client heightmap reconstruction after receiving a column has the same vanilla off-by-one. */
@Mixin(value = Chunk.class, priority = 1100)
public abstract class MixinClientChunkHeightmap {

    @ModifyVariable(method = "generateHeightMap", at = @At(value = "STORE", ordinal = 0), index = 4, require = 1)
    private int hohenangst$includeUpperSectionTop(int original) {
        return HeightRange.rebuildScanStart(original);
    }
}
