package industries.angelic.hohenangst.mixins.late.natura;

import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.hohenangst.compat.PlantedGrowthHeights;

/** Natura's hopseed bush: its three 256 limits lifted for a planted sapling, which also roots where it stands. */
@Pseudo
@Mixin(targets = "mods.natura.worldgen.BushTreeGen", remap = false)
public abstract class MixinNaturaBushGrowthHeight {

    @ModifyConstant(
        method = { "generate(Lnet/minecraft/world/World;Ljava/util/Random;III)Z",
            "func_76484_a(Lnet/minecraft/world/World;Ljava/util/Random;III)Z" },
        constant = @Constant(intValue = 256),
        remap = false,
        require = 3,
        expect = 3,
        allow = 3)
    private int hohenangst$plantedHeight(int original, World world, Random random, int x, int y, int z) {
        return PlantedGrowthHeights.naturaBound(world, original);
    }

    @Inject(
        method = "findGround(Lnet/minecraft/world/World;III)I",
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1)
    private void hohenangst$plantedRoot(World world, int x, int y, int z, CallbackInfoReturnable<Integer> result) {
        Integer root = PlantedGrowthHeights.naturaGround(world, y);
        if (root != null) result.setReturnValue(root);
    }
}
