package industries.angelic.hohenangst.mixins.early;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.item.ItemBlock", priority = 1100)
public abstract class MixinItemBlock {

    @ModifyConstant(method = { "onItemUse" }, constant = @Constant(intValue = 255), require = 1)
    private int hohenangst$constant0(int original) {
        return 512;
    }
}
