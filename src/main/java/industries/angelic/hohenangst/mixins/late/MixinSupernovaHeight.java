package industries.angelic.hohenangst.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import industries.angelic.hohenangst.world.HeightRange;

/** The block access Supernova's light engines read through. */
@Pseudo
@Mixin(
    targets = { "com.mitchej123.supernova.light.engine.SafeBlockAccess",
        "com.mitchej123.supernova.light.engine.FaceOcclusion$FakeBlockAccess" },
    remap = false)
public abstract class MixinSupernovaHeight {

    // IBlockAccess.getHeight: named func_72800_K outside the dev environment.
    @ModifyConstant(method = { "getHeight", "func_72800_K" }, constant = @Constant(intValue = 256), require = 1)
    private int hohenangst$height(int original) {
        return HeightRange.HEIGHT;
    }
}
