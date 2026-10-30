package industries.angelic.hohenangst.mixins.late;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Pseudo
@Mixin(
    targets = "com.gtnewhorizons.angelica.rendering.celeritas.AngelicaRenderSectionManager",
    priority = 500,
    remap = false)
public abstract class MixinAngelicaSections {

    @ModifyConstant(method = { "create" }, constant = @Constant(intValue = 16), require = 1)
    private static int hohenangst$constant0(int original) {
        return 32;
    }

    // A player can reconnect or reload shaders while flying above the ceiling.
    // Older Angelica waits for a real section, not the nonexistent camera section.
    // Angelica 2.2.30 removed this update override and its initial-section wait;
    // the inherited graph-search path has no corresponding packSection call.
    // Keep only this obsolete workaround optional; the 32-section geometry stays required.
    @Dynamic("Legacy Angelica initial-section wait, absent from 2.2.30")
    @ModifyArg(
        method = "update",
        at = @At(value = "INVOKE", target = "Lorg/embeddedt/embeddium/impl/util/PositionUtil;packSection(III)J"),
        index = 1,
        require = 0)
    private int hohenangst$initialCameraSection(int sectionY) {
        return Math.max(0, Math.min(31, sectionY));
    }
}
