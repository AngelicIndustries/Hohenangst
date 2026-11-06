package industries.angelic.hohenangst.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

// Beddium builds its render sections for 16 storage sections, whatever their grouping into MEGA chunks.
@Pseudo
@Mixin(
    targets = "com.ventooth.beddium.modules.TerrainRendering.ArchaicRenderSectionManager",
    priority = 500,
    remap = false)
public abstract class MixinBeddiumSections {

    @ModifyConstant(method = "create", constant = @Constant(intValue = 16), require = 1)
    private static int hohenangst$sections(int original) {
        return 32;
    }
}
