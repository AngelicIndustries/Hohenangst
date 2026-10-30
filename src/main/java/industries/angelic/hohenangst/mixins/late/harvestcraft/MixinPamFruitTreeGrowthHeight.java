package industries.angelic.hohenangst.mixins.late.harvestcraft;

import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import industries.angelic.hohenangst.compat.PlantedGrowthHeights;

/** HarvestCraft's fruit trees: their three 256 limits, lifted for a planted sapling; the fruit is untouched. */
@Pseudo
@Mixin(
    targets = { "com.pam.harvestcraft.WorldGenPamFruitTree", "com.pam.harvestcraft.WorldGenPamFruitLogTree" },
    remap = false)
public abstract class MixinPamFruitTreeGrowthHeight {

    @ModifyConstant(
        method = { "generate", "func_76484_a" },
        constant = @Constant(intValue = 256),
        remap = false,
        require = 3,
        expect = 3,
        allow = 3)
    private int hohenangst$plantedHeight(int original, World world, Random random, int x, int y, int z) {
        return PlantedGrowthHeights.orchardBound(world, original);
    }
}
