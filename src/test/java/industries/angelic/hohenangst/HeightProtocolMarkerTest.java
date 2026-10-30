package industries.angelic.hohenangst;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import cpw.mods.fml.common.Mod;

public class HeightProtocolMarkerTest {

    @Test
    public void declaresExactInertHeightProtocolContainer() {
        Mod marker = HeightProtocolMarker.class.getAnnotation(Mod.class);

        assertNotNull(marker);
        assertEquals("hohenangst_height_protocol", marker.modid());
        assertEquals("Hohenangst Height Protocol", marker.name());
        assertEquals("height512-v1", marker.version());
        assertEquals("*", marker.acceptableRemoteVersions());
        assertFalse(marker.useMetadata());
        assertEquals(0, HeightProtocolMarker.class.getDeclaredFields().length);
        assertEquals(0, HeightProtocolMarker.class.getDeclaredMethods().length);
    }
}
