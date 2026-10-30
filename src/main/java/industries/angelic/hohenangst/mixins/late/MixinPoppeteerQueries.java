package industries.angelic.hohenangst.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/** Keep optional automation queries consistent with the actual world envelope. */
@Pseudo
@Mixin(targets = "org.fentanylsolutions.poppeteer.control.ClientApi", remap = false)
public abstract class MixinPoppeteerQueries {

    @ModifyConstant(method = "queryBlocks", constant = @Constant(intValue = 255), require = 1)
    private int hohenangst$queryHeight(int original) {
        return 511;
    }
}
