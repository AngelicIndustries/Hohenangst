package industries.angelic.hohenangst.mixins.early;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = net.minecraft.block.BlockVine.class, priority = 1100)
public abstract class MixinBlockVine {

    @ModifyConstant(method = "updateTick", constant = @Constant(intValue = 255), require = 1)
    private int hohenangst$height(int original) {
        return 511;
    }
}
