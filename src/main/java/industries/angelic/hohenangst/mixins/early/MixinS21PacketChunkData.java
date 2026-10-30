package industries.angelic.hohenangst.mixins.early;

import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.play.server.S21PacketChunkData", priority = 1100)
public abstract class MixinS21PacketChunkData {

    @Shadow
    private int field_149283_c;

    @ModifyVariable(
        method = "<init>(Lnet/minecraft/world/chunk/Chunk;ZI)V",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0,
        require = 1)
    private static int hohenangst$fullColumn(int mask, Chunk chunk, boolean full, int originalMask) {
        return full && mask == 65535 ? -1 : mask;
    }

    @Inject(method = "writePacketData", at = @At("RETURN"), require = 1)
    private void hohenangst$writeMask(PacketBuffer buffer, CallbackInfo ci) {
        buffer.writeInt(field_149283_c);
    }

    @Inject(method = "readPacketData", at = @At("RETURN"), require = 1)
    private void hohenangst$readMask(PacketBuffer buffer, CallbackInfo ci) {
        field_149283_c = buffer.readInt();
    }
}
