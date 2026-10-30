package industries.angelic.hohenangst.mixins.late.natura;

import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.hohenangst.compat.PlantedGrowthHeights;

/** Natura's rare saplings grow in the planted scope (PlantedGrowthHeights). */
@Pseudo
@Mixin(targets = "mods.natura.blocks.trees.OverworldSapling", remap = false)
public abstract class MixinNaturaOverworldSaplingGrowthHeight {

    @WrapMethod(
        method = { "func_149878_d(Lnet/minecraft/world/World;IIILjava/util/Random;)V",
            "growTree(Lnet/minecraft/world/World;IIILjava/util/Random;)V" },
        remap = false)
    private void hohenangst$planted(World world, int x, int y, int z, Random random, Operation<Void> original) {
        PlantedGrowthHeights
            .naturaSapling(world, false, world.getBlockMetadata(x, y, z), () -> original.call(world, x, y, z, random));
    }
}
