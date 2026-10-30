package industries.angelic.hohenangst.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import industries.angelic.hohenangst.world.HeightRange;

/**
 * Distant Horizons' 1.7.10 chunk wrapper reads every chunk as 256 high while its level wrappers take the world's
 * height, so a chunk with blocks above 256 fails its bounds check and drops out of the LODs. Until Distant Horizons
 * reads the world's height itself (then these constants are gone and nothing here applies), give it the full range.
 */
@Pseudo
@Mixin(targets = "com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper", remap = false)
public abstract class MixinDistantHorizonsChunkHeight {

    @ModifyConstant(
        method = "getHeight(Lnet/minecraft/world/chunk/Chunk;)I",
        constant = @Constant(intValue = 255),
        require = 0)
    private static int hohenangst$height(int original) {
        return HeightRange.HEIGHT - 1;
    }

    @ModifyConstant(
        method = "getExclusiveMaxBuildHeight(Lnet/minecraft/world/chunk/Chunk;)I",
        constant = @Constant(intValue = 256),
        require = 0)
    private static int hohenangst$exclusiveMaxBuildHeight(int original) {
        return HeightRange.HEIGHT;
    }

    /** Without a height map the column scan starts here and climbs only through solid blocks. */
    @ModifyConstant(method = "getSolidHeightMapValue", constant = @Constant(intValue = 255), require = 0)
    private int hohenangst$solidHeightFallback(int original) {
        return HeightRange.HEIGHT - 1;
    }
}
