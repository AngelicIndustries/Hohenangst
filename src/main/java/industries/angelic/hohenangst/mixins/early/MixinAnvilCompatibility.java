package industries.angelic.hohenangst.mixins.early;

import org.spongepowered.asm.mixin.Mixin;

/** Attaches the post-apply bridge after ChunkAPI contributes its NBT section reader. */
@Mixin(targets = "net.minecraft.world.chunk.storage.AnvilChunkLoader", priority = 1100)
public abstract class MixinAnvilCompatibility {
}
