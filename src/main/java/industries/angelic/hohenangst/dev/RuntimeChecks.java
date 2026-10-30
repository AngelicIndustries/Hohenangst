package industries.angelic.hohenangst.dev;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import com.falsepattern.chunk.internal.DataRegistryImpl;

import industries.angelic.hohenangst.Hohenangst;
import industries.angelic.hohenangst.compat.BoundaryChecks;
import io.netty.buffer.Unpooled;

/** Only invoked explicitly on the disposable height-audit world; never on a player's save. */
public final class RuntimeChecks {

    private RuntimeChecks() {}

    public static void run(MinecraftServer server) {
        StringBuilder report = new StringBuilder();
        try {
            WorldServer world = server.worldServerForDimension(0);
            require(
                world.getWorldInfo()
                    .getWorldName()
                    .startsWith("hohenangst-height-audit"),
                "Disposable audit world required");
            require(world.getHeight() == 512 && server.getBuildLimit() == 512, "World and server limits");
            for (int cx = -1; cx <= 1; cx++) for (int cz = -1; cz <= 1; cz++) world.getChunkFromChunkCoords(cx, cz);
            Chunk chunk = world.getChunkFromChunkCoords(0, 0);
            require(
                chunk.getBlockStorageArray().length == 32 && chunk.entityLists.length == 32,
                "Storage and entity sections");
            int[] levels = { 255, 256, 300, 495, 496, 511 };
            for (int i = 0; i < levels.length; i++) {
                int y = levels[i], x = i + 2;
                world.setBlockToAir(x, y, 2);
                require(world.setBlock(x, y, 2, Blocks.wool, i, 3), "Place block at " + y);
                require(
                    world.getBlock(x, y, 2) == Blocks.wool && world.getBlockMetadata(x, y, 2) == i,
                    "Read block at " + y);
            }
            require(!world.setBlock(2, 512, 2, Blocks.stone, 0, 3), "Reject Y=512");
            require(!world.setBlock(2, -1, 2, Blocks.stone, 0, 3), "Reject negative Y");
            net.minecraft.world.ChunkCache pathCache = new net.minecraft.world.ChunkCache(
                world,
                0,
                250,
                0,
                15,
                511,
                15,
                0);
            require(pathCache.getBlock(4, 300, 2) == Blocks.wool, "Upper pathfinding cache block");
            require(pathCache.getBlockMetadata(4, 300, 2) == 2, "Upper pathfinding cache metadata");
            world.setBlock(9, 511, 2, Blocks.stone, 0, 3);
            require(chunk.getHeightValue(9, 2) == 512, "Heightmap preserves 512");
            world.setBlockToAir(9, 511, 2);
            require(chunk.getHeightValue(9, 2) < 512, "Heightmap removal relights top");
            world.setBlock(8, 300, 8, Blocks.chest, 0, 3);
            require(
                world.getTileEntity(8, 300, 8) instanceof TileEntityChest,
                "Upper chest survives Hodgepodge bounds guard");
            ((TileEntityChest) world.getTileEntity(8, 300, 8))
                .setInventorySlotContents(0, new ItemStack(Blocks.diamond_block, 7));
            world.setBlock(11, 300, 11, Blocks.glowstone, 0, 3);
            flushLighting(world);
            require(
                world.getSavedLightValue(EnumSkyBlock.Block, 12, 300, 11) == 14,
                "Upper lamp propagates block light");
            world.setBlockToAir(11, 300, 11);
            flushLighting(world);
            require(
                world.getSavedLightValue(EnumSkyBlock.Block, 12, 300, 11) == 0,
                "Upper lamp removal darkens neighbors");
            require(PathPoint.makeHash(0, 256, 0) != PathPoint.makeHash(1, 0, 0), "Path hashes retain ninth Y bit");
            report.append(
                "PASS block bounds, metadata, heightmaps, tile entities, light propagation/removal, path hashes\n");

            EntityCow[] cows = new EntityCow[3];
            int[] entityLevels = { 255, 256, 511 };
            for (int i = 0; i < cows.length; i++) {
                cows[i] = new EntityCow(world);
                cows[i].setPosition(6.5, entityLevels[i], 6.5);
                require(world.spawnEntityInWorld(cows[i]), "Spawn entity at " + entityLevels[i]);
                require(chunk.entityLists[entityLevels[i] >> 4].contains(cows[i]), "Correct entity section");
            }

            // Every section, including signed bit 31, must be represented in the initial chunk payload.
            for (int section = 0; section < 32; section++) world.setBlock(1, section * 16 + 1, 1, Blocks.stone, 0, 3);
            S21PacketChunkData full = new S21PacketChunkData(chunk, true, 65535);
            S21PacketChunkData decoded = (S21PacketChunkData) roundTrip(full);
            int mask = (Integer) field(decoded, "field_149283_c");
            require(mask == -1, "S21 full 32-bit section mask");
            byte[] payload = (byte[]) field(decoded, "field_149278_f");
            Chunk copy = new Chunk(world, 0, 0);
            for (int section = 0; section < 32; section++)
                copy.getBlockStorageArray()[section] = new ExtendedBlockStorage(section << 4, true);
            DataRegistryImpl.readFromBuffer(copy, mask, true, payload);
            for (int i = 0; i < levels.length; i++) require(
                copy.getBlock(i + 2, levels[i], 2) == Blocks.wool && copy.getBlockMetadata(i + 2, levels[i], 2) == i,
                "S21 block payload at " + levels[i]);
            S26PacketMapChunkBulk bulk = (S26PacketMapChunkBulk) roundTrip(
                new S26PacketMapChunkBulk(Arrays.asList(chunk)));
            require(((int[]) field(bulk, "field_149265_c"))[0] == -1, "S26 full 32-bit section mask");
            for (int y : new int[] { 255, 256, 511 }) {
                checkHeight(new C07PacketPlayerDigging(), "field_149509_b", y);
                checkHeight(new C08PacketPlayerBlockPlacement(), "field_149581_b", y);
                checkHeight(new S28PacketEffect(), "field_149247_d", y);
            }
            report.append("PASS S21/S26 and interaction/effect packet round trips at 255/256/511\n");

            int[] boundary = new int[32];
            boundary[0] = Integer.MIN_VALUE;
            boundary[31] = -1;
            ((BoundaryChecks) chunk).hohenangst$setNeighborChecks(boundary);
            AnvilChunkLoader loader = new AnvilChunkLoader(new File("hohenangst-storage-audit"));
            loader.saveChunk(world, chunk);
            loader.saveExtraData();
            Chunk restored = loader.loadChunk(world, 0, 0);
            require(restored != null && restored.getBlockStorageArray().length == 32, "NBT section allocation");
            for (int i = 0; i < levels.length; i++) require(
                restored.getBlock(i + 2, levels[i], 2) == Blocks.wool
                    && restored.getBlockMetadata(i + 2, levels[i], 2) == i,
                "NBT block at " + levels[i]);
            require(restored.func_150806_e(8, 300, 8) instanceof TileEntityChest, "NBT upper tile entity");
            TileEntityChest chest = (TileEntityChest) restored.func_150806_e(8, 300, 8);
            require(chest.getStackInSlot(0).stackSize == 7, "NBT upper chest contents");
            for (int i = 0; i < cows.length; i++) {
                boolean found = false;
                for (Object object : restored.entityLists[entityLevels[i] >> 4]) {
                    Entity entity = (Entity) object;
                    if (entity.getUniqueID()
                        .equals(cows[i].getUniqueID())) found = true;
                }
                require(found, "NBT entity at " + entityLevels[i]);
                world.removeEntity(cows[i]);
            }
            report.append("PASS entity section assignment and Anvil persistence at 255/256/511\n");
            if (cpw.mods.fml.common.Loader.isModLoaded("archaicfix")) require(
                Arrays.equals(boundary, ((BoundaryChecks) restored).hohenangst$getNeighborChecks()),
                "NBT 32-bit Phosphor boundary flags");
            report.append("PASS actual Anvil save/reload including upper chest contents and boundary-light masks\n");
            report.insert(0, "PASS\n");
        } catch (Throwable failure) {
            report.insert(0, "FAIL " + failure + "\n");
            Hohenangst.LOG.error("Runtime height audit failed", failure);
        } finally {
            try {
                Files.write(
                    new File("height-audit-result.txt").toPath(),
                    report.toString()
                        .getBytes(StandardCharsets.UTF_8));
            } catch (Exception failure) {
                Hohenangst.LOG.error("Cannot write audit report", failure);
            }
            Hohenangst.LOG.info(report.toString());
            server.initiateShutdown();
        }
    }

    private static void flushLighting(WorldServer world) throws Exception {
        if (cpw.mods.fml.common.Loader.isModLoaded("archaicfix")) {
            Object engine = world.getClass()
                .getMethod("getLightingEngine")
                .invoke(world);
            engine.getClass()
                .getMethod("processLightUpdates")
                .invoke(engine);
        }
    }

    private static Packet roundTrip(Packet packet) throws Exception {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        try {
            packet.writePacketData(buffer);
            Packet result = packet.getClass()
                .newInstance();
            result.readPacketData(buffer);
            require(buffer.readableBytes() == 0, "Packet consumed exactly its payload");
            return result;
        } finally {
            buffer.release();
        }
    }

    private static void checkHeight(Packet packet, String name, int y) throws Exception {
        Field field = packet.getClass()
            .getDeclaredField(name);
        field.setAccessible(true);
        field.setInt(packet, y);
        require(
            ((Integer) field(roundTrip(packet), name)) == y,
            "Height round trip " + packet.getClass()
                .getSimpleName() + " at " + y);
    }

    private static Object field(Object object, String name) throws Exception {
        Field field = object.getClass()
            .getDeclaredField(name);
        field.setAccessible(true);
        return field.get(object);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
