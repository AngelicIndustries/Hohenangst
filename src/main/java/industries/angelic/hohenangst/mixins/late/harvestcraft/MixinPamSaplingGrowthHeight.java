package industries.angelic.hohenangst.mixins.late.harvestcraft;

import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.hohenangst.compat.PlantedGrowthHeights;

/** HarvestCraft's fruit saplings grow in the planted scope (PlantedGrowthHeights). */
@Pseudo
@Mixin(targets = "com.pam.harvestcraft.BlockPamSapling", remap = false)
public abstract class MixinPamSaplingGrowthHeight {

    @WrapMethod(method = { "growTree", "func_149878_d" }, remap = false)
    private void hohenangst$planted(World world, int x, int y, int z, Random random, Operation<Void> original) {
        PlantedGrowthHeights.orchardSapling(world, () -> original.call(world, x, y, z, random));
    }
}
