package industries.angelic.hohenangst.mixins.early;

import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import industries.angelic.hohenangst.compat.BoundaryChecks;

@Mixin(Chunk.class)
public abstract class MixinBoundaryChecks implements BoundaryChecks {

    @Unique
    private int[] hohenangst$neighborChecks;

    public int[] hohenangst$getNeighborChecks() {
        return hohenangst$neighborChecks;
    }

    public void hohenangst$setNeighborChecks(int[] checks) {
        hohenangst$neighborChecks = checks;
    }
}
