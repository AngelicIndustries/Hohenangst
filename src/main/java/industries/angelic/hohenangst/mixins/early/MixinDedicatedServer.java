package industries.angelic.hohenangst.mixins.early;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.server.dedicated.DedicatedServer", priority = 1100)
public abstract class MixinDedicatedServer {

    @ModifyConstant(method = { "startServer" }, constant = @Constant(intValue = 256), require = 1)
    private int hohenangst$constant0(int original) {
        return 512;
    }
}
