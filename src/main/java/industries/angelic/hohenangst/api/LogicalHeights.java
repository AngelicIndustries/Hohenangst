package industries.angelic.hohenangst.api;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.world.World;

/**
 * A dimension's logical height: the part of the column its generator fills, which portals search and build in.
 * Providers report their own (a vanilla Nether says 128 even in 512-block storage). A mod whose generator fills a
 * world taller than its provider reports sets the real figure here; {@code World.getActualHeight} then returns it.
 */
public final class LogicalHeights {

    private static final Map<World, Integer> HEIGHTS = Collections.synchronizedMap(new WeakHashMap<>());

    private LogicalHeights() {}

    /** Sets the world's logical height, at most its storage height. */
    public static void set(World world, int height) {
        if (world == null || height < 16 || height > world.getHeight())
            throw new IllegalArgumentException("Logical height " + height + " outside this world's storage");
        HEIGHTS.put(world, height);
    }

    /** The world's set logical height, or null when its provider's own applies. */
    public static Integer get(World world) {
        return HEIGHTS.get(world);
    }
}
