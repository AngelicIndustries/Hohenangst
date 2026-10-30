package industries.angelic.hohenangst.compat;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.world.chunk.Chunk;

/** Extra light values live outside the packed 61-bit position; horizontal bounds stay unchanged. */
public final class PhosphorBridge {

    private static final String KEY = "HohenangstNeighborLightChecks";

    private PhosphorBridge() {}

    public static long recordLight(int light, long position, Object engine) {
        ((LightQueue) engine).hohenangst$pushLight(light);
        return position;
    }

    public static int nextLight(Object engine) {
        return ((LightQueue) engine).hohenangst$popLight();
    }

    public static void writeChecks(Chunk chunk, NBTTagCompound nbt) {
        int[] checks = ((BoundaryChecks) chunk).hohenangst$getNeighborChecks();
        if (checks != null) nbt.setIntArray(KEY, checks);
    }

    public static void readChecks(Chunk chunk, NBTTagCompound nbt) {
        int[] checks = null;
        if (nbt.hasKey(KEY, 11)) {
            checks = nbt.getIntArray(KEY);
            if (checks.length != 32) throw new IllegalArgumentException("Invalid Hohenangst lighting boundary array");
        } else if (nbt.hasKey("NeighborLightChecks", 9)) {
            NBTTagList old = (NBTTagList) nbt.getTagList("NeighborLightChecks", 2)
                .copy();
            if (old.tagCount() == 32) {
                checks = new int[32];
                for (int i = 0; i < 32; i++) checks[i] = ((NBTTagShort) old.removeTag(0)).func_150289_e() & 65535;
            }
        }
        ((BoundaryChecks) chunk).hohenangst$setNeighborChecks(checks);
    }
}
