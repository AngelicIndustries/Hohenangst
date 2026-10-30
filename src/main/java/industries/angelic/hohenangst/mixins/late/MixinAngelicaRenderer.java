package industries.angelic.hohenangst.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Pseudo
@Mixin(targets = "com.gtnewhorizons.angelica.rendering.celeritas.CeleritasWorldRenderer", priority = 500, remap = false)
public abstract class MixinAngelicaRenderer {

    @ModifyConstant(method = { "getMaximumBuildHeight" }, constant = @Constant(intValue = 256), require = 1)
    private int hohenangst$constant0(int original) {
        return 512;
    }
}
