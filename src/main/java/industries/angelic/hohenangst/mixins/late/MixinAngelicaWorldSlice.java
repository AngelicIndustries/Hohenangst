package industries.angelic.hohenangst.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Pseudo
@Mixin(targets = "com.gtnewhorizons.angelica.rendering.celeritas.world.WorldSlice", priority = 500, remap = false)
public abstract class MixinAngelicaWorldSlice {

    @ModifyConstant(
        // The IBlockAccess override has an SRG name in Angelica's release jar.
        method = { "getLightBrightnessForSkyBlocks", "func_72802_i", "getRawSkyLight" },
        constant = @Constant(intValue = 256),
        require = 2)
    private int hohenangst$constant0(int original) {
        return 512;
    }
}
