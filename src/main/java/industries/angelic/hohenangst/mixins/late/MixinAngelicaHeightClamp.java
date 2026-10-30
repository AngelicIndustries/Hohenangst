package industries.angelic.hohenangst.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Pseudo
@Mixin(targets = "com.gtnewhorizons.angelica.compat.mojang.CompatMathHelper", remap = false)
public abstract class MixinAngelicaHeightClamp {

    @ModifyConstant(method = "clampHeight", constant = @Constant(intValue = 255), require = 1)
    private static int hohenangst$lightClamp(int original) {
        return 511;
    }
}
