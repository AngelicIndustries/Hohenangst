package industries.angelic.hohenangst.mixins.late.natura;

import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import industries.angelic.hohenangst.compat.PlantedGrowthHeights;

/** Natura's willow: its three 128 limits, lifted for a planted sapling. */
@Pseudo
@Mixin(targets = "mods.natura.worldgen.WillowGen", remap = false)
public abstract class MixinNaturaWillowGrowthHeight {

    @ModifyConstant(
        method = { "generate(Lnet/minecraft/world/World;Ljava/util/Random;III)Z",
            "func_76484_a(Lnet/minecraft/world/World;Ljava/util/Random;III)Z" },
        constant = @Constant(intValue = 128),
        remap = false,
        require = 3,
        expect = 3,
        allow = 3)
    private int hohenangst$plantedHeight(int original, World world, Random random, int x, int y, int z) {
        return PlantedGrowthHeights.naturaBound(world, original);
    }
}
