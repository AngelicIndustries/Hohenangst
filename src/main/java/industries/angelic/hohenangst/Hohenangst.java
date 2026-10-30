package industries.angelic.hohenangst;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/** 512-block world height for Minecraft 1.7.10. */
@Mod(
    modid = Hohenangst.MODID,
    name = "Hohenangst",
    version = Tags.VERSION,
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:chunkapi@[0.8.4,);required-after:unimixins",
    guiFactory = "industries.angelic.hohenangst.client.ConfigGuiFactory",
    customProperties = { @Mod.CustomProperty(k = "license", v = "LGPLv3"),
        @Mod.CustomProperty(k = "issueTrackerUrl", v = "https://github.com/AngelicIndustries/Hohenangst/issues"),
        @Mod.CustomProperty(k = "iconFile", v = "assets/hohenangst/icon.png"),
        @Mod.CustomProperty(k = "backgroundFile", v = "assets/hohenangst/background.png") })
public final class Hohenangst {

    public static final String MODID = "hohenangst";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.load(event.getModConfigurationDirectory());
        cpw.mods.fml.common.FMLCommonHandler.instance()
            .bus()
            .register(new Config());
        LOG.info("Hohenangst loaded: 512 blocks, Y=0..511.");
    }

    @Mod.EventHandler
    public void init(cpw.mods.fml.common.event.FMLInitializationEvent event) {
        com.falsepattern.chunk.api.DataRegistry
            .registerDataManager(new industries.angelic.hohenangst.world.HeightDataManager(), 0);
        if (Config.debugMode) {
            for (cpw.mods.fml.common.ModContainer mod : cpw.mods.fml.common.Loader.instance()
                .getActiveModList()) {
                LOG.info("Compatibility environment: {} {}", mod.getModId(), mod.getVersion());
            }
        }
    }

    @Mod.EventHandler
    public void started(cpw.mods.fml.common.event.FMLServerStartedEvent event) {
        if (Boolean.getBoolean("hohenangst.sectionGallery"))
            industries.angelic.hohenangst.dev.SectionGallery.start(net.minecraft.server.MinecraftServer.getServer());
        if (Boolean.getBoolean("hohenangst.heightAudit"))
            industries.angelic.hohenangst.dev.RuntimeChecks.run(net.minecraft.server.MinecraftServer.getServer());
    }

    @cpw.mods.fml.common.network.NetworkCheckHandler
    public boolean compatible(java.util.Map<String, String> remoteMods, cpw.mods.fml.relauncher.Side remoteSide) {
        return Tags.VERSION.equals(remoteMods.get(MODID));
    }
}
