package industries.angelic.hohenangst.mixins.late.natura;

import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.hohenangst.compat.PlantedGrowthHeights;

/** A planted redwood grows only where the whole tree fits under the world's top; it is never cut off. */
@Pseudo
@Mixin(targets = "mods.natura.worldgen.RedwoodTreeGen", remap = false)
public abstract class MixinNaturaRedwoodGrowthEnvelope {

    @WrapMethod(
        method = { "generate(Lnet/minecraft/world/World;Ljava/util/Random;III)Z",
            "func_76484_a(Lnet/minecraft/world/World;Ljava/util/Random;III)Z" },
        remap = false)
    private boolean hohenangst$wholeRedwood(World world, Random random, int x, int y, int z,
        Operation<Boolean> original) {
        return PlantedGrowthHeights.redwoodFits(world, y) && original.call(world, random, x, y, z);
    }
}
