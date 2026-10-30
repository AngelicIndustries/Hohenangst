package industries.angelic.hohenangst.network;

import java.nio.ByteBuffer;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import com.falsepattern.chunk.api.DataManager;

public final class ColumnCodec {

    private ColumnCodec() {}

    public static void transfer(DataManager.CubicPacketDataManager manager, Chunk chunk, int mask, ByteBuffer buffer,
        boolean write) {
        ExtendedBlockStorage[] sections = chunk.getBlockStorageArray();
        if (sections.length != 32) throw new IllegalStateException("Expected 32 sections, got " + sections.length);
        for (int i = 0; i < 32; i++) {
            if ((mask & (1 << i)) == 0) continue;
            if (sections[i] == null) throw new IllegalStateException("Selected section " + i + " is missing");
            if (write) manager.writeToBuffer(chunk, sections[i], buffer);
            else manager.readFromBuffer(chunk, sections[i], buffer);
        }
    }
}
