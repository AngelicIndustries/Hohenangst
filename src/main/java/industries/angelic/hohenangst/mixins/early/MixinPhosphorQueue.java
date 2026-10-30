package industries.angelic.hohenangst.mixins.early;

import java.util.ArrayDeque;

import net.minecraft.world.EnumSkyBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.hohenangst.compat.LightQueue;

@Pseudo
@Mixin(targets = "org.embeddedt.archaicfix.lighting.world.lighting.LightingEngine", remap = false)
public abstract class MixinPhosphorQueue implements LightQueue {

    @Unique
    private final ArrayDeque<Integer> hohenangst$initialLight = new ArrayDeque<>();

    public void hohenangst$pushLight(int light) {
        hohenangst$initialLight.addLast(light);
    }

    public int hohenangst$popLight() {
        return hohenangst$initialLight.removeFirst();
    }

    @Inject(
        method = "scheduleLightUpdate(Lnet/minecraft/world/EnumSkyBlock;III)V",
        at = @At("HEAD"),
        cancellable = true,
        require = 1)
    private void hohenangst$bounds(EnumSkyBlock type, int x, int y, int z, CallbackInfo ci) {
        if (y < 0 || y >= 512) ci.cancel();
    }
}
