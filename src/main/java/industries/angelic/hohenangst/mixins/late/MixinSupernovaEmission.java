package industries.angelic.hohenangst.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import industries.angelic.hohenangst.world.HeightRange;

/** Colored light from tile entities (positional emitters) above 256. */
@Pseudo
@Mixin(targets = "com.mitchej123.supernova.light.DynamicEmissionSnapshots", remap = false)
public abstract class MixinSupernovaEmission {

    @ModifyConstant(method = { "isTileEmitter", "track" }, constant = @Constant(intValue = 255), require = 2)
    private static int hohenangst$top(int original) {
        return HeightRange.HEIGHT - 1;
    }
}
