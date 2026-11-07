package industries.angelic.hohenangst.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import industries.angelic.hohenangst.world.HeightRange;

/** Changing the light blend mode redraws the whole world, not just up to 256 (vanilla renderer only). */
@Pseudo
@Mixin(targets = "com.mitchej123.supernova.ClientProxy", remap = false)
public abstract class MixinSupernovaRedraw {

    @ModifyConstant(method = "rebuildTintedGeometry", constant = @Constant(intValue = 255), require = 1)
    private static int hohenangst$top(int original) {
        return HeightRange.HEIGHT - 1;
    }
}
