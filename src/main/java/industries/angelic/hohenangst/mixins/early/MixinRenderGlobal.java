package industries.angelic.hohenangst.mixins.early;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.client.renderer.RenderGlobal", priority = 1100)
public abstract class MixinRenderGlobal {

    @ModifyConstant(method = { "<init>" }, constant = @Constant(intValue = 16), require = 1)
    private int hohenangst$constant0(int original) {
        return 32;
    }
}
