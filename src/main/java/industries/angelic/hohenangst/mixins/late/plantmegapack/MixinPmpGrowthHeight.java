package industries.angelic.hohenangst.mixins.late.plantmegapack;

import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

/**
 * Plant Mega Pack's plants grow right in a tall world: a double plant at the world's top does not grow (PMP's failed
 * double branch would fall through to a plain stage step), nothing above the top counts as air, and vines climb to
 * the top instead of 255. Written against PMP 4.33 with plantmegapackfix 1333d4e.
 */
@Pseudo
@Mixin(targets = "plantmegapack.block.PMPBlockPlant", remap = false)
public abstract class MixinPmpGrowthHeight {

    @Shadow(remap = false)
    public abstract boolean isImmersedPlant();

    @Inject(method = "growPlant", at = @At("HEAD"), cancellable = true, require = 1)
    private void hohenangst$wholeDoubleAtTop(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> result) {
        if (world.getHeight() <= 256 || y != world.getHeight() - 1
            || world.getBlockMetadata(x, y, z) != 1
            || isImmersedPlant()) return;
        try {
            Object data = getClass().getField("plantData")
                .get(this);
            Object attributes = data.getClass()
                .getField("attributes")
                .get(data);
            if ("DOUBLE".equals(
                attributes.getClass()
                    .getField("growthType")
                    .get(attributes)
                    .toString()))
                result.setReturnValue(false);
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("PMP's double growth type is not where 4.33 has it", error);
        }
    }

    @WrapOperation(
        method = { "growPlant", "growStalkPlantFromBottom" },
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isAirBlock(III)Z", remap = true),
        require = 1)
    private boolean hohenangst$noAirBeyondTheWorld(World world, int x, int y, int z, Operation<Boolean> original) {
        if (world.getHeight() > 256 && (y < 0 || y >= world.getHeight())) return false;
        return original.call(world, x, y, z);
    }

    @ModifyConstant(method = "updateVineTick", constant = @Constant(intValue = 255), require = 1)
    private int hohenangst$vineCeiling(int literal, World world, int x, int y, int z, Random random) {
        return world.getHeight() > 256 ? world.getHeight() - 1 : literal;
    }
}
