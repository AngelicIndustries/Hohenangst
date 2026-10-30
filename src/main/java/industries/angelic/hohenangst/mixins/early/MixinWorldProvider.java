package industries.angelic.hohenangst.mixins.early;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.world.WorldProvider", priority = 1100)
public abstract class MixinWorldProvider {

    @ModifyConstant(
        method = { "getHeight", "getActualHeight" },
        constant = @Constant(intValue = 256),
        remap = false,
        require = 1)
    private int hohenangst$constant0(int original) {
        return 512;
    }
}
