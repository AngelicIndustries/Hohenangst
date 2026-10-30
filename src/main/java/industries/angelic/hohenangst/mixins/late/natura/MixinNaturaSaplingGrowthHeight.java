package industries.angelic.hohenangst.mixins.late.natura;

import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.hohenangst.compat.PlantedGrowthHeights;

/** Natura's ordinary saplings grow in the planted scope (PlantedGrowthHeights). */
@Pseudo
@Mixin(targets = "mods.natura.blocks.trees.NSaplingBlock", remap = false)
public abstract class MixinNaturaSaplingGrowthHeight {

    @WrapMethod(
        method = { "func_149878_d(Lnet/minecraft/world/World;IIILjava/util/Random;)V",
            "growTree(Lnet/minecraft/world/World;IIILjava/util/Random;)V" },
        remap = false)
    private void hohenangst$planted(World world, int x, int y, int z, Random random, Operation<Void> original) {
        int metadata = world.getBlockMetadata(x, y, z);
        PlantedGrowthHeights.naturaSapling(world, true, metadata, () -> {
            // A redwood that cannot fit is refused before Forge's sapling event draws or the sapling dispatcher writes.
            if (metadata % 8 == 0 && !PlantedGrowthHeights.redwoodFits(world, y)) return;
            original.call(world, x, y, z, random);
        });
    }
}
