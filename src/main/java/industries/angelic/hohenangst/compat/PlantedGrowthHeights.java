package industries.angelic.hohenangst.compat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.World;

import industries.angelic.hohenangst.api.PlantedGrowth;

/**
 * The planted-sapling scope in which the Natura and HarvestCraft growth mixins lift their tree generators' height
 * limits (256, the willow's 128) to the world's height. A generator run outside it (world generation, another mod's
 * replay) keeps its limits, as does one inside a frame registered with PlantedGrowth. Written against the audited
 * Natura 2.8.19 and 2.8.24 and HarvestCraft 1.3.13-GTNH (GrowthMixinVersions).
 */
public final class PlantedGrowthHeights {

    private static final ThreadLocal<World> NATURA = new ThreadLocal<>();
    private static final ThreadLocal<World> ORCHARD = new ThreadLocal<>();
    private static final Set<String> NATURA_VERSIONS = new HashSet<>(Arrays.asList("2.8.19", "2.8.24"));
    private static final String HARVESTCRAFT_VERSION = "1.3.13-GTNH";
    /** Natura's redwood: up to 139 trunk levels, a top node 4 above, a 9-block canopy. */
    private static final int REDWOOD_REACH = 152;

    private PlantedGrowthHeights() {}

    /** The Natura growth mixins apply to one installed Natura (one mod id alias) of an audited version. */
    public static boolean naturaMixins(Set<String> loadedMods, String version) {
        boolean capital = loadedMods.contains("Natura"), lowercase = loadedMods.contains("natura");
        // Two aliases at once are an ambiguous provider, not proof of a particular class layout.
        return capital != lowercase && NATURA_VERSIONS.contains(version);
    }

    public static boolean harvestCraftMixins(Set<String> loadedMods, String version) {
        return loadedMods.contains("harvestcraft") && HARVESTCRAFT_VERSION.equals(version);
    }

    /**
     * Grows a Natura sapling in the planted scope when its species is one whose tree tests a height limit: metadata 0,
     * 1, 2, 6 and 7 of the ordinary saplings, 0 to 4 of the rare ones. The sapling's own dispatcher, Forge's growth
     * event, its random and its restoration on a failed growth stay as they are.
     */
    public static void naturaSapling(World world, boolean ordinarySapling, int metadata, Runnable growth) {
        within(NATURA, affected(ordinarySapling, metadata) ? world : null, growth);
    }

    static boolean affected(boolean ordinarySapling, int metadata) {
        if (metadata < 0 || metadata > 15) return false;
        int species = metadata % 8;
        return ordinarySapling ? species == 0 || species == 1 || species == 2 || species == 6 || species == 7
            : species <= 4;
    }

    /** A Natura tree generator's height limit in the current scope. */
    public static int naturaBound(World world, int original) {
        return original == 256 || original == 128 ? bound(NATURA, world, original) : original;
    }

    /**
     * Where a planted hopseed bush roots: its own findGround ignores the sapling and starts at sea level plus 64. In
     * the lifted scope it keeps the sapling's height (its soil checks remain), else null for Natura's own.
     */
    public static Integer naturaGround(World world, int requestedY) {
        int bound = naturaBound(world, 256);
        return bound > 256 && requestedY >= 0 && requestedY < bound ? Integer.valueOf(requestedY) : null;
    }

    /** Whether a whole planted redwood fits under the world's top: rejected before any random draw or write. */
    public static boolean redwoodFits(World world, int rootY) {
        int bound = naturaBound(world, 256);
        return bound <= 256 || rootY + REDWOOD_REACH < bound;
    }

    /** Grows a HarvestCraft fruit sapling in the planted scope. */
    public static void orchardSapling(World world, Runnable growth) {
        within(ORCHARD, world, growth);
    }

    /** A HarvestCraft fruit tree's height limit in the current scope. */
    public static int orchardBound(World world, int original) {
        return original == 256 ? bound(ORCHARD, world, original) : original;
    }

    private static int bound(ThreadLocal<World> scope, World world, int original) {
        if (world == null || world.isRemote || scope.get() != world || PlantedGrowth.limitsKept(world)) return original;
        return lifted(original, world.getHeight());
    }

    static int lifted(int original, int worldHeight) {
        return worldHeight > 256 ? worldHeight : original;
    }

    private static void within(ThreadLocal<World> scope, World world, Runnable growth) {
        World previous = scope.get();
        if (world != null) scope.set(world);
        else scope.remove();
        try {
            growth.run();
        } finally {
            if (previous == null) scope.remove();
            else scope.set(previous);
        }
    }
}
