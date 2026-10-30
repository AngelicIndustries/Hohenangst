package industries.angelic.hohenangst.compat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class PlantedGrowthHeightsTest {

    private static Set<String> mods(String... ids) {
        return new HashSet<>(Arrays.asList(ids));
    }

    @Test
    public void naturaMixinsNeedOneAliasOfAnAuditedBuild() {
        assertTrue(PlantedGrowthHeights.naturaMixins(mods("Natura"), "2.8.19"));
        assertTrue(PlantedGrowthHeights.naturaMixins(mods("natura"), "2.8.24"));
        assertFalse(PlantedGrowthHeights.naturaMixins(mods("Natura", "natura"), "2.8.24"));
        assertFalse(PlantedGrowthHeights.naturaMixins(mods("Natura"), "2.8.25"));
        assertFalse(PlantedGrowthHeights.naturaMixins(mods(), "2.8.24"));
    }

    @Test
    public void harvestCraftMixinsNeedTheAuditedBuild() {
        assertTrue(PlantedGrowthHeights.harvestCraftMixins(mods("harvestcraft"), "1.3.13-GTNH"));
        assertFalse(PlantedGrowthHeights.harvestCraftMixins(mods("harvestcraft"), "1.3.12-GTNH"));
        assertFalse(PlantedGrowthHeights.harvestCraftMixins(mods(), "1.3.13-GTNH"));
    }

    @Test
    public void onlySpeciesWithAHeightLimitAreScoped() {
        for (int meta : new int[] { 0, 1, 2, 6, 7, 8, 14 }) assertTrue(PlantedGrowthHeights.affected(true, meta));
        for (int meta : new int[] { 3, 4, 5, 11, -1, 16 }) assertFalse(PlantedGrowthHeights.affected(true, meta));
        for (int meta : new int[] { 0, 4, 12 }) assertTrue(PlantedGrowthHeights.affected(false, meta));
        for (int meta : new int[] { 5, 7, 13 }) assertFalse(PlantedGrowthHeights.affected(false, meta));
    }

    @Test
    public void limitsLiftToATallWorldOnly() {
        assertEquals(512, PlantedGrowthHeights.lifted(256, 512));
        assertEquals(512, PlantedGrowthHeights.lifted(128, 512));
        assertEquals(256, PlantedGrowthHeights.lifted(256, 256));
    }
}
