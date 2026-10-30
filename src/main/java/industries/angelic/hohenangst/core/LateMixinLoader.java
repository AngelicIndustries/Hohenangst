package industries.angelic.hohenangst.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import industries.angelic.hohenangst.compat.PlantedGrowthHeights;

@LateMixin
public final class LateMixinLoader implements ILateMixinLoader {

    public String getMixinConfig() {
        return "mixins.hohenangst.late.json";
    }

    public List<String> getMixins(Set<String> loadedMods) {
        List<String> result = new ArrayList<>(Arrays.asList("MixinSectionCodecs"));
        if (loadedMods.contains("endlessids")) result.add("MixinEndlessSectionCodecs");
        if (FMLLaunchHandler.side()
            .isClient() && loadedMods.contains("angelica"))
            result.addAll(
                Arrays.asList(
                    "MixinAngelicaRenderer",
                    "MixinAngelicaHeightClamp",
                    "MixinAngelicaSections",
                    "MixinAngelicaWorldSlice",
                    "MixinAngelicaClonedSection"));
        if (loadedMods.contains("poppeteer")) result.add("MixinPoppeteerQueries");
        if (loadedMods.contains("distanthorizons")) result.add("MixinDistantHorizonsChunkHeight");
        // Planted saplings grow whole trees above 256 (PlantedGrowthHeights), on the audited builds only.
        if (PlantedGrowthHeights.naturaMixins(loadedMods, version(loadedMods.contains("Natura") ? "Natura" : "natura")))
            result.addAll(
                Arrays.asList(
                    "natura.MixinNaturaSaplingGrowthHeight",
                    "natura.MixinNaturaOverworldSaplingGrowthHeight",
                    "natura.MixinNaturaEucalyptusGrowthHeight",
                    "natura.MixinNaturaRareGrowthHeight",
                    "natura.MixinNaturaWillowGrowthHeight",
                    "natura.MixinNaturaRedwoodGrowthEnvelope",
                    "natura.MixinNaturaBushGrowthHeight",
                    "natura.MixinNaturaDarkwoodGrowthHeight",
                    "natura.MixinNaturaFusewoodGrowthHeight"));
        if (loadedMods.contains("plantmegapack") && "4.33".equals(version("plantmegapack"))
            && "1333d4e".equals(version("plantmegapackfix"))) result.add("plantmegapack.MixinPmpGrowthHeight");
        if (PlantedGrowthHeights.harvestCraftMixins(loadedMods, version("harvestcraft"))) result.addAll(
            Arrays.asList("harvestcraft.MixinPamSaplingGrowthHeight", "harvestcraft.MixinPamFruitTreeGrowthHeight"));
        return result;
    }

    private static String version(String modId) {
        ModContainer mod = Loader.instance()
            .getIndexedModList()
            .get(modId);
        return mod == null ? null : mod.getVersion();
    }
}
