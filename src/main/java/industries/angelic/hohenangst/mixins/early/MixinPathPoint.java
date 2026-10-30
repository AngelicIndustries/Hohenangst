package industries.angelic.hohenangst.mixins.early;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.pathfinding.PathPoint", priority = 1100)
public abstract class MixinPathPoint {

    @Inject(method = "makeHash", at = @At("HEAD"), cancellable = true, require = 1)
    private static void hohenangst$heightHash(int x, int y, int z, CallbackInfoReturnable<Integer> ci) {
        ci.setReturnValue((y & 511) | ((x & 2047) << 9) | ((z & 4095) << 20));
    }
}
