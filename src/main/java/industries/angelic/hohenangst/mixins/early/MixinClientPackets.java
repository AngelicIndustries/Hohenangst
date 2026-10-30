package industries.angelic.hohenangst.mixins.early;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.client.network.NetHandlerPlayClient", priority = 1100)
public abstract class MixinClientPackets {

    @ModifyConstant(
        method = { "handleChunkData", "handleMapChunkBulk" },
        constant = @Constant(intValue = 256),
        require = 1)
    private int hohenangst$constant0(int original) {
        return 512;
    }
}
