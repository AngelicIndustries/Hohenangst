package industries.angelic.hohenangst.world;

public final class HeightRange {

    public static final int HEIGHT = 512;
    public static final int SECTIONS = 32;

    private HeightRange() {}

    /**
     * Vanilla scans opacity at (cursor - 1), but initializes cursor one row too low.
     * Keep legacy lower-half rebuilds unchanged; include the top row of upper sections.
     */
    public static int rebuildScanStart(int vanillaStart) {
        return vanillaStart >= 256 ? vanillaStart + 1 : vanillaStart;
    }

    public static int sectionMask(int minY, int maxY) {
        int bottom = Math.max(0, minY), top = Math.min(511, maxY);
        if (bottom > top) return 0;
        int first = bottom >> 4, count = (top >> 4) - first + 1;
        return (int) (((1L << count) - 1L) << first);
    }
}
