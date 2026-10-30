package industries.angelic.hohenangst.mixins.early;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Upper blocks cannot be represented by vanilla's packed short update list. */
@Mixin(targets = "net.minecraft.server.management.PlayerManager$PlayerInstance", priority = 1100)
public abstract class MixinPlayerInstance {

    @Shadow
    private List<EntityPlayerMP> playersWatchingChunk;
    @Shadow
    private ChunkCoordIntPair chunkLocation;

    @Shadow
    public abstract void sendToAllPlayersWatchingChunk(Packet packet);

    @Inject(method = "flagChunkForUpdate", at = @At("HEAD"), cancellable = true, require = 1)
    private void hohenangst$upperBlockUpdate(int x, int y, int z, CallbackInfo ci) {
        if (y < 256) return;
        ci.cancel();
        if (y >= 512 || playersWatchingChunk.isEmpty()) return;
        World world = playersWatchingChunk.get(0).worldObj;
        int worldX = (chunkLocation.chunkXPos << 4) + x;
        int worldZ = (chunkLocation.chunkZPos << 4) + z;
        sendToAllPlayersWatchingChunk(new S23PacketBlockChange(worldX, y, worldZ, world));
        TileEntity tile = world.getTileEntity(worldX, y, worldZ);
        if (tile != null) {
            Packet description = tile.getDescriptionPacket();
            if (description != null) sendToAllPlayersWatchingChunk(description);
        }
    }
}
