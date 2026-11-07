package industries.angelic.hohenangst.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions("industries.angelic.hohenangst.core")
public final class EarlyMixinLoader implements IEarlyMixinLoader, IFMLLoadingPlugin {

    public String getMixinConfig() {
        return "mixins.hohenangst.early.json";
    }

    public List<String> getMixins(Set<String> loadedCoreMods) {
        List<String> result = new ArrayList<>(
            Arrays.asList(
                "MixinWorld",
                "MixinWorldLogicalHeight",
                "MixinChunkCache",
                "MixinChunk",
                "MixinWorldProvider",
                "MixinItemBlock",
                "MixinEntityPlayerMP",
                "MixinEntityFallingBlock",
                "MixinBlockLilyPad",
                "MixinBlockMushroom",
                "MixinBlockVine",
                "MixinC07PacketPlayerDigging",
                "MixinC08PacketPlayerBlockPlacement",
                "MixinS28PacketEffect",
                "MixinS21PacketChunkData",
                "MixinS26PacketMapChunkBulk",
                "MixinPathPoint",
                "MixinPlayerInstance",
                "MixinAnvilCompatibility",
                "MixinBoundaryChecks",
                "MixinPhosphorQueue"));
        // Supernova replaces vanilla's relighting with its own engine, which WorldUtil bounds make height-aware.
        if (!loadedCoreMods.contains("com.mitchej123.supernova.core.SupernovaCore"))
            result.add("MixinChunkVanillaLighting");
        if (FMLLaunchHandler.side()
            .isClient())
            result.addAll(
                Arrays.asList(
                    "MixinIntegratedServer",
                    "MixinClientPackets",
                    "MixinChunkCacheLighting",
                    "MixinClientChunkHeightmap"));
        else result.add("MixinDedicatedServer");
        // Angelica and Beddium replace the vanilla terrain renderer; late mixins give theirs the height.
        if (FMLLaunchHandler.side()
            .isClient() && !loadedCoreMods.contains("com.gtnewhorizons.angelica.loading.AngelicaTweaker")
            && !loadedCoreMods.contains("com.ventooth.beddium.asm.CoreLoadingPlugin"))
            result.addAll(Arrays.asList("MixinRenderGlobal", "MixinRenderGlobalHeight"));
        return result;
    }

    public String[] getASMTransformerClass() {
        return new String[] { "industries.angelic.hohenangst.core.PhosphorTransformer" };
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {}

    public String getAccessTransformerClass() {
        return null;
    }
}
