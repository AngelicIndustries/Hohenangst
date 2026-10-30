package industries.angelic.hohenangst;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import industries.angelic.hohenangst.world.HeightRange;

public class HeightRangeTest {

    @Test
    public void fullColumnUsesAllBits() {
        assertEquals(-1, HeightRange.sectionMask(0, 511));
        assertEquals(-1, HeightRange.sectionMask(Integer.MIN_VALUE, Integer.MAX_VALUE));
        assertEquals(Integer.MIN_VALUE, HeightRange.sectionMask(496, 511));
        assertEquals(0x18000, HeightRange.sectionMask(255, 256));
    }

    @Test
    public void outsideOrReversedRangesAreEmpty() {
        assertEquals(0, HeightRange.sectionMask(512, 1024));
        assertEquals(0, HeightRange.sectionMask(-16, -1));
        assertEquals(0, HeightRange.sectionMask(400, 300));
    }
}
