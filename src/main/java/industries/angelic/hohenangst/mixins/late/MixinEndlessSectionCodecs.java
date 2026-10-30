package industries.angelic.hohenangst.mixins.late;

import java.nio.ByteBuffer;

import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.falsepattern.chunk.api.DataManager;

import industries.angelic.hohenangst.network.ColumnCodec;

@Pseudo
@Mixin(
    targets = { "com.falsepattern.endlessids.managers.BlockIDManager",
        "com.falsepattern.endlessids.managers.BlockMetaManager" },
    remap = false)
public abstract class MixinEndlessSectionCodecs {

    @Inject(method = "maxPacketSize", at = @At("HEAD"), cancellable = true, require = 1)
    private void hohenangst$capacity(CallbackInfoReturnable<Integer> ci) {
        ci.setReturnValue(32 * ((DataManager.CubicPacketDataManager) this).maxPacketSizeCubic());
    }

    @Inject(
        method = "writeToBuffer(Lnet/minecraft/world/chunk/Chunk;IZLjava/nio/ByteBuffer;)V",
        at = @At("HEAD"),
        cancellable = true,
        require = 1)
    private void hohenangst$write(Chunk chunk, int mask, boolean full, ByteBuffer buffer, CallbackInfo ci) {
        ColumnCodec.transfer((DataManager.CubicPacketDataManager) this, chunk, mask, buffer, true);
        ci.cancel();
    }

    @Inject(
        method = "readFromBuffer(Lnet/minecraft/world/chunk/Chunk;IZLjava/nio/ByteBuffer;)V",
        at = @At("HEAD"),
        cancellable = true,
        require = 1)
    private void hohenangst$read(Chunk chunk, int mask, boolean full, ByteBuffer buffer, CallbackInfo ci) {
        ColumnCodec.transfer((DataManager.CubicPacketDataManager) this, chunk, mask, buffer, false);
        ci.cancel();
    }
}
