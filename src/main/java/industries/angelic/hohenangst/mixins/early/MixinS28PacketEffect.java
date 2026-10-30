package industries.angelic.hohenangst.mixins.early;

import net.minecraft.network.PacketBuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.play.server.S28PacketEffect", priority = 1100)
public abstract class MixinS28PacketEffect {

    @Shadow
    private int field_149247_d;

    @Inject(method = "writePacketData", at = @At("RETURN"), require = 1)
    private void hohenangst$writeHeight(PacketBuffer buffer, CallbackInfo ci) {
        buffer.writeInt(field_149247_d);
    }

    @Inject(method = "readPacketData", at = @At("RETURN"), require = 1)
    private void hohenangst$readHeight(PacketBuffer buffer, CallbackInfo ci) {
        field_149247_d = buffer.readInt();
    }
}
