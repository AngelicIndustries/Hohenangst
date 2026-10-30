package industries.angelic.hohenangst.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;

import com.falsepattern.chunk.api.DataManager;

import industries.angelic.hohenangst.compat.BoundaryChecks;
import industries.angelic.hohenangst.compat.PhosphorBridge;

/** ChunkAPI persists the height format marker and Phosphor's widened deferred-light masks. */
public final class HeightDataManager implements DataManager.ChunkDataManager {

    public String domain() {
        return "hohenangst";
    }

    public String id() {
        return "height";
    }

    public String version() {
        return "1";
    }

    public String newInstallDescription() {
        return "Extend chunk capacity to Y=0..511.";
    }

    public String uninstallMessage() {
        return "This world can contain blocks above Y=255. Keep Hohenangst installed to preserve them.";
    }

    public String versionChangeMessage(String previous) {
        return version().equals(previous) ? null : "Hohenangst height storage format changed.";
    }

    public boolean chunkPrivilegedAccess() {
        return false;
    }

    public void writeChunkToNBT(Chunk chunk, NBTTagCompound nbt) {
        nbt.setInteger("Height", HeightRange.HEIGHT);
        PhosphorBridge.writeChecks(chunk, nbt);
    }

    public void readChunkFromNBT(Chunk chunk, NBTTagCompound nbt) {
        if (nbt.hasKey("Height") && nbt.getInteger("Height") != HeightRange.HEIGHT)
            throw new IllegalStateException("Unsupported saved world height");
        PhosphorBridge.readChecks(chunk, nbt);
    }

    public void cloneChunk(Chunk from, Chunk to) {
        int[] checks = ((BoundaryChecks) from).hohenangst$getNeighborChecks();
        ((BoundaryChecks) to).hohenangst$setNeighborChecks(checks == null ? null : checks.clone());
    }
}
