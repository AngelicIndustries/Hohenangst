package industries.angelic.hohenangst.core;

import java.util.List;
import java.util.Set;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.IntInsnNode;
import org.spongepowered.asm.lib.tree.LdcInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/** Targeted post-apply adaptations for ChunkAPI and Hodgepodge. Companion mods own their height integration. */
public final class MergedMixinCompatibility implements IMixinConfigPlugin {

    public void onLoad(String mixinPackage) {}

    public String getRefMapperConfig() {
        return null;
    }

    public boolean shouldApplyMixin(String target, String mixin) {
        return true;
    }

    public void acceptTargets(Set<String> mine, Set<String> others) {}

    public List<String> getMixins() {
        return null;
    }

    public void preApply(String target, ClassNode node, String mixin, IMixinInfo info) {}

    public void postApply(String target, ClassNode node, String mixin, IMixinInfo info) {
        if (mixin.endsWith(".MixinChunk")) {
            for (MethodNode method : node.methods) {
                if (method.name.contains("hodgepodge$validateTileInBounds")) replace(method, 256, 512);
            }
        } else if (mixin.endsWith(".MixinAnvilCompatibility")) {
            int count = 0;
            for (MethodNode method : node.methods) {
                if (method.name.contains("readSubChunks")
                    && method.desc.equals("(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/nbt/NBTTagCompound;)V"))
                    count += replace(method, 16, 32);
            }
            if (count != 1) throw new IllegalStateException("ChunkAPI 0.8.4 section reader not found: " + count);
        }
    }

    private static int replace(MethodNode method, int from, int to) {
        int changes = 0;
        for (AbstractInsnNode instruction = method.instructions.getFirst(); instruction != null;) {
            AbstractInsnNode next = instruction.getNext();
            Integer value = null;
            if (instruction instanceof IntInsnNode && instruction.getOpcode() != Opcodes.NEWARRAY)
                value = Integer.valueOf(((IntInsnNode) instruction).operand);
            else if (instruction instanceof LdcInsnNode && ((LdcInsnNode) instruction).cst instanceof Integer)
                value = (Integer) ((LdcInsnNode) instruction).cst;
            if (value != null && value == from) {
                method.instructions.set(instruction, new LdcInsnNode(to));
                changes++;
            }
            instruction = next;
        }
        return changes;
    }
}
