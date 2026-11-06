package industries.angelic.hohenangst.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

// Boxes above the build height are drawn without a section check; the build height is 512, not 256.
@Pseudo
@Mixin(targets = "com.ventooth.beddium.modules.TerrainRendering.CeleritasWorldRenderer", priority = 500, remap = false)
public abstract class MixinBeddiumRenderer {

    @ModifyConstant(method = "isBoxVisible", constant = @Constant(doubleValue = 254.5D), require = 1)
    private double hohenangst$top(double original) {
        return 510.5D;
    }
}
