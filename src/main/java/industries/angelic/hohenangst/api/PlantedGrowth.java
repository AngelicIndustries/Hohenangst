package industries.angelic.hohenangst.api;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import net.minecraft.world.World;

/**
 * Saplings planted above the vanilla build limit grow whole trees: Natura's and HarvestCraft's tree generators test
 * fixed limits (256, the willow's 128), which Hohen Angst lifts to the world's height while a planted sapling grows. A
 * mod that runs those generators itself in a vanilla-height frame (a world generator replaying them for its own
 * terrain) registers when it does, and they keep their limits then.
 */
public final class PlantedGrowth {

    private static final List<Predicate<World>> FRAMES = new CopyOnWriteArrayList<>();

    private PlantedGrowth() {}

    /** While {@code frame} holds for a world on the current thread, tree generators keep their own height limits. */
    public static void keepLimitsWhile(Predicate<World> frame) {
        if (frame == null) throw new IllegalArgumentException("A frame is required");
        FRAMES.add(frame);
    }

    public static boolean limitsKept(World world) {
        for (Predicate<World> frame : FRAMES) if (frame.test(world)) return true;
        return false;
    }
}
