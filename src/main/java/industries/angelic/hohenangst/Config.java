package industries.angelic.hohenangst;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class Config {

    public static Configuration configuration;
    public static boolean debugMode;

    public static void load(File directory) {
        configuration = new Configuration(new File(directory, "hohenangst/hohenangst.cfg"));
        sync();
    }

    public static void sync() {
        debugMode = configuration.get("general", "debugMode", false, "Log compatibility details at startup.")
            .setLanguageKey("hohenangst.config.debugMode")
            .getBoolean() || System.getenv("MCMODDING_DEBUG_MODE") != null;
        if (configuration.hasChanged()) configuration.save();
    }

    @SubscribeEvent
    public void changed(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (Hohenangst.MODID.equals(event.modID)) sync();
    }
}
