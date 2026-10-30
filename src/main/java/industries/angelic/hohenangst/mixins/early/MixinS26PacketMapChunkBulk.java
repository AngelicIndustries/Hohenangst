package industries.angelic.hohenangst.mixins.early;

import net.minecraft.network.PacketBuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.play.server.S26PacketMapChunkBulk", priority = 1100)
public abstract class MixinS26PacketMapChunkBulk {

    @Shadow
    private int[] field_149265_c;

    @ModifyConstant(method = "<init>(Ljava/util/List;)V", constant = @Constant(intValue = 65535), require = 1)
    private int hohenangst$allSections(int original) {
        return -1;
    }

    @Inject(method = "writePacketData", at = @At("RETURN"), require = 1)
    private void hohenangst$writeMasks(PacketBuffer buffer, CallbackInfo ci) {
        for (int mask : field_149265_c) buffer.writeInt(mask);
    }

    @Inject(method = "readPacketData", at = @At("RETURN"), require = 1)
    private void hohenangst$readMasks(PacketBuffer buffer, CallbackInfo ci) {
        for (int i = 0; i < field_149265_c.length; i++) field_149265_c[i] = buffer.readInt();
    }
}
