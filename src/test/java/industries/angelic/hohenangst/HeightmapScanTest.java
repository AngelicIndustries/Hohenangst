package industries.angelic.hohenangst;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import industries.angelic.hohenangst.world.HeightRange;

public class HeightmapScanTest {

    @Test
    public void upperRebuildIncludesTopVoxelAndKeepsLegacyBehavior() {
        for (int section = 0; section < 32; section++) {
            int vanillaStart = section * 16 + 15;
            int cursor = HeightRange.rebuildScanStart(vanillaStart);
            assertEquals(section < 16 ? vanillaStart : vanillaStart + 1, cursor);
        }
        assertEquals(512, HeightRange.rebuildScanStart(511));
    }
}
