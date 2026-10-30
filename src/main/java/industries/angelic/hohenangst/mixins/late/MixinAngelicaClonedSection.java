package industries.angelic.hohenangst.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Pseudo
@Mixin(
    targets = "com.gtnewhorizons.angelica.rendering.celeritas.world.cloned.ClonedChunkSection",
    priority = 500,
    remap = false)
public abstract class MixinAngelicaClonedSection {

    @ModifyConstant(method = { "isOutOfBuildLimitVertically" }, constant = @Constant(intValue = 256), require = 1)
    private static int hohenangst$constant0(int original) {
        return 512;
    }
}
