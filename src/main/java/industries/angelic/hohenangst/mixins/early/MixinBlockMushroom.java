package industries.angelic.hohenangst.mixins.early;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = net.minecraft.block.BlockMushroom.class, priority = 1100)
public abstract class MixinBlockMushroom {

    @ModifyConstant(method = "canBlockStay", constant = @Constant(intValue = 256), require = 1)
    private int hohenangst$height(int original) {
        return 512;
    }
}
