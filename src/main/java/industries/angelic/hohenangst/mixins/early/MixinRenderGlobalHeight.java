package industries.angelic.hohenangst.mixins.early;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.client.renderer.RenderGlobal", priority = 1100)
public abstract class MixinRenderGlobalHeight {

    @ModifyConstant(method = "loadRenderers", constant = @Constant(intValue = 16, ordinal = 0), require = 1)
    private int hohenangst$rendererHeight(int original) {
        return 32;
    }
}
