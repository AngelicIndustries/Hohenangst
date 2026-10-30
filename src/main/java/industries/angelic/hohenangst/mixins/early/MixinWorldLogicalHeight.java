package industries.angelic.hohenangst.mixins.early;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.hohenangst.api.LogicalHeights;

/** A logical height set through {@link LogicalHeights} wins over the provider's, whichever mod's provider it is. */
@Mixin(World.class)
public abstract class MixinWorldLogicalHeight {

    @Inject(method = "getActualHeight", at = @At("HEAD"), cancellable = true, require = 1)
    private void hohenangst$logicalHeight(CallbackInfoReturnable<Integer> result) {
        Integer height = LogicalHeights.get((World) (Object) this);
        if (height != null) result.setReturnValue(height);
    }
}
