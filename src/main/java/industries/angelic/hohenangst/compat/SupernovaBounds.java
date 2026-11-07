package industries.angelic.hohenangst.compat;

import industries.angelic.hohenangst.world.HeightRange;

/** Supernova lights sections 0..15 unless told otherwise; its engines read the bounds when worlds and chunks load. */
public final class SupernovaBounds {

    private SupernovaBounds() {}

    public static void apply() {
        try {
            Class.forName("com.mitchej123.supernova.util.WorldUtil")
                .getMethod("setBounds", int.class, int.class)
                .invoke(null, 0, HeightRange.SECTIONS - 1);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                "Supernova no longer has WorldUtil.setBounds; light above 256 would break",
                e);
        }
    }
}
