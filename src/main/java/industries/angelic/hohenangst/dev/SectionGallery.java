package industries.angelic.hohenangst.dev;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import industries.angelic.hohenangst.Hohenangst;

/** Opt-in, server-built visual fixture. Clients only need the ordinary height protocol. */
public final class SectionGallery {

    public static final String WORLD_NAME = "hohenangst-section-gallery";
    private static final String[] DIGITS = { "111/101/101/101/111", "010/110/010/010/111", "111/001/111/100/111",
        "111/001/111/001/111", "101/101/111/001/001", "111/100/111/001/111", "111/100/111/101/111",
        "111/001/010/010/010", "111/101/111/101/111", "111/101/111/001/111" };
    private int placed;

    public static void start(MinecraftServer server) {
        WorldServer world = server.worldServerForDimension(0);
        if (!WORLD_NAME.equals(
            world.getWorldInfo()
                .getWorldName())) {
            throw new IllegalStateException("Section gallery requires its separate debug world: " + WORLD_NAME);
        }
        SectionGallery gallery = new SectionGallery();
        File marker = new File(
            world.getSaveHandler()
                .getWorldDirectory(),
            "hohenangst-gallery-v1.txt");
        if (!marker.exists()) {
            gallery.build(world);
            try {
                world.saveAllChunks(true, null);
                world.getChunkProvider()
                    .saveExtraData();
                Files.write(
                    marker.toPath(),
                    ("Four columns; 32 sections each; Y=0..511; " + gallery.placed + " block writes.\n")
                        .getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new IllegalStateException("Could not save section gallery", e);
            }
        }
        world.setSpawnLocation(0, 257, 64);
        world.setWorldTime(6000);
        world.getGameRules()
            .setOrCreateGameRule("doDaylightCycle", "false");
        world.getGameRules()
            .setOrCreateGameRule("doMobSpawning", "false");
        world.getGameRules()
            .setOrCreateGameRule("keepInventory", "true");
        world.getWorldInfo()
            .setRaining(false);
        world.getWorldInfo()
            .setThundering(false);
        FMLCommonHandler.instance()
            .bus()
            .register(gallery);
        Hohenangst.LOG.info("Section gallery ready: 128 diamond frames, labels 000..496, spawn at Y=264.");
    }

    private void build(WorldServer world) {
        if (world.getHeight() != 512) throw new IllegalStateException("Gallery requires 512-block storage");
        for (int x : new int[] { -32, 16 }) {
            for (int z : new int[] { -32, 16 }) {
                for (int section = 0; section < 32; section++) {
                    int base = section * 16;
                    for (int a = 0; a < 16; a++) {
                        for (int b : new int[] { 0, 15 }) {
                            put(world, x + a, base, z + b, Blocks.diamond_block);
                            put(world, x + a, base + 15, z + b, Blocks.diamond_block);
                            put(world, x + b, base, z + a, Blocks.diamond_block);
                            put(world, x + b, base + 15, z + a, Blocks.diamond_block);
                            for (int c : new int[] { 0, 15 }) {
                                put(world, x + b, base + a, z + c, Blocks.diamond_block);
                            }
                        }
                    }
                    // One centered label avoids overlapping front/back text through the open frame.
                    String label = String.format(Locale.ROOT, "%03d", base);
                    number(world, label, x + 2, base + 5, z + 8);
                }
                if (world.getBlock(x, 511, z) != Blocks.diamond_block)
                    throw new IllegalStateException("Missing gallery ceiling frame");
            }
        }
        // A small transparent landing deck; all the remaining space stays void.
        for (int x = -8; x < 8; x++) for (int z = 56; z < 72; z++) {
            put(world, x, 256, z, x == -8 || x == 7 || z == 56 || z == 71 ? Blocks.diamond_block : Blocks.glass);
        }
        sign(world, -3, 257, 58, "DIAMOND FRAMES", "16 x 16 x 16", "32 sections", "Y = 0 to 511");
        sign(world, 0, 257, 58, "GLOWING NUMBERS", "Section base Y", "000, 016, ...496", "Fly through gaps");
        sign(world, 3, 257, 58, "OLD CEILING: 255", "Deck at Y = 256", "NEW CEILING: 511", "Above is air");
        Hohenangst.LOG.info("Built section gallery with {} block writes.", placed);
    }

    private void number(WorldServer world, String text, int x, int y, int z) {
        for (int digit = 0; digit < text.length(); digit++) {
            String[] rows = DIGITS[text.charAt(digit) - '0'].split("/");
            for (int row = 0; row < 5; row++) for (int column = 0; column < 3; column++) {
                if (rows[row].charAt(column) != '1') continue;
                int offset = digit * 4 + column;
                put(world, x + offset, y + 4 - row, z, Blocks.glowstone);
            }
        }
    }

    private void sign(WorldServer world, int x, int y, int z, String... lines) {
        put(world, x, y, z, Blocks.standing_sign);
        TileEntitySign sign = (TileEntitySign) world.getTileEntity(x, y, z);
        if (sign == null) throw new IllegalStateException("Missing gallery sign");
        System.arraycopy(lines, 0, sign.signText, 0, 4);
        sign.markDirty();
    }

    private void put(WorldServer world, int x, int y, int z, Block block) {
        if (world.setBlock(x, y, z, block, 0, 2)) placed++;
    }

    @SubscribeEvent
    public void joined(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (!WORLD_NAME.equals(
            player.worldObj.getWorldInfo()
                .getWorldName()))
            return;
        player.setGameType(WorldSettings.GameType.CREATIVE);
        player.playerNetServerHandler.setPlayerLocation(0.5, 264, 64.5, 180, 8);
        player.capabilities.allowFlying = true;
        player.capabilities.isFlying = true;
        player.sendPlayerAbilities();
        player.addChatMessage(
            new ChatComponentText(
                "Diamond frames: 16-block sections. Glowing numbers: base Y (000..496). Ceiling: 511."));
    }
}
