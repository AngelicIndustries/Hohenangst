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
        if (FMLLaunchHandler.side()
            .isClient())
            result.addAll(
                Arrays.asList(
                    "MixinIntegratedServer",
                    "MixinClientPackets",
                    "MixinChunkCacheLighting",
                    "MixinClientChunkHeightmap"));
        else result.add("MixinDedicatedServer");
        if (FMLLaunchHandler.side()
            .isClient() && !loadedCoreMods.contains("com.gtnewhorizons.angelica.loading.AngelicaTweaker"))
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
