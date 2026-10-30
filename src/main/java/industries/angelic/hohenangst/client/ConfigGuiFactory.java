package industries.angelic.hohenangst.client;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import industries.angelic.hohenangst.Config;
import industries.angelic.hohenangst.Hohenangst;

public final class ConfigGuiFactory implements IModGuiFactory {

    public void initialize(Minecraft minecraft) {}

    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return Screen.class;
    }

    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

    public static final class Screen extends GuiConfig {

        public Screen(GuiScreen parent) {
            super(
                parent,
                new ConfigElement(Config.configuration.getCategory("general")).getChildElements(),
                Hohenangst.MODID,
                false,
                false,
                "Hohenangst — 512 blocks (Y=0–511)");
        }
    }
}
