package industries.angelic.hohenangst.core;

import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/** ASM is restricted to Phosphor's packed queue expressions and short boundary-mask ABI. */
public final class PhosphorTransformer implements IClassTransformer {

    private static final String PREFIX = "org/embeddedt/archaicfix/";
    private static final String BRIDGE = "industries/angelic/hohenangst/compat/PhosphorBridge";
    private static final String CHECKS = "industries/angelic/hohenangst/compat/BoundaryChecks";

    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null || transformedName == null) return bytes;
        String internal = transformedName.replace('.', '/');
        boolean engine = internal.equals(PREFIX + "lighting/world/lighting/LightingEngine");
        boolean hooks = internal.equals(PREFIX + "lighting/world/lighting/LightingHooks");
        boolean manager = internal.equals(PREFIX + "chunkapi/ChunkLightingDataManager");
        if (!engine && !hooks && !manager) return bytes;
        ClassNode node = new ClassNode();
        new ClassReader(bytes).accept(node, 0);
        for (MethodNode method : node.methods) {
            if (engine) patchEngine(method, node.name);
            else patchBoundaries(method, hooks);
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }

    private static void patchEngine(MethodNode method, String owner) {
        if (method.name.equals("decodeWorldCoord")) replaceLong(method, 255L, 511L);
        if (method.name.equals("fetchNeighborDataFromCursor")) replaceLong(method, 1L << 60, 1L << 61);
        if (!method.name.equals("processLightUpdatesForTypeInner")) return;
        int writes = 0, reads = 0;
        for (AbstractInsnNode instruction = method.instructions.getFirst(); instruction != null;) {
            AbstractInsnNode next = instruction.getNext();
            // newLight I2L, 60, LSHL, ALOAD 0, GETFIELD curData, LOR
            if (instruction.getOpcode() == Opcodes.I2L && intConstant(next) == 60) {
                AbstractInsnNode end = next;
                for (int i = 0; i < 4; i++) end = end.getNext();
                if (end.getOpcode() != Opcodes.LOR) throw new IllegalStateException("Unknown Phosphor light packing");
                InsnList replacement = new InsnList();
                replacement.add(new VarInsnNode(Opcodes.ALOAD, 0));
                replacement.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "curData", "J"));
                replacement.add(new VarInsnNode(Opcodes.ALOAD, 0));
                replacement.add(
                    new MethodInsnNode(Opcodes.INVOKESTATIC, BRIDGE, "recordLight", "(IJLjava/lang/Object;)J", false));
                next = end.getNext();
                replaceRange(method, instruction, end, replacement);
                writes++;
            } else if (instruction instanceof FieldInsnNode && ((FieldInsnNode) instruction).name.equals("curData")
                && intConstant(next) == 60) {
                    // ALOAD 0, GETFIELD curData, 60, LSHR, 15L, LAND, L2I
                    AbstractInsnNode start = instruction.getPrevious();
                    AbstractInsnNode end = next;
                    for (int i = 0; i < 4; i++) end = end.getNext();
                    if (end.getOpcode() != Opcodes.L2I)
                        throw new IllegalStateException("Unknown Phosphor light unpacking");
                    InsnList replacement = new InsnList();
                    replacement.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    replacement.add(
                        new MethodInsnNode(Opcodes.INVOKESTATIC, BRIDGE, "nextLight", "(Ljava/lang/Object;)I", false));
                    next = end.getNext();
                    replaceRange(method, start, end, replacement);
                    reads++;
                }
            instruction = next;
        }
        if (writes != 1 || reads != 1)
            throw new IllegalStateException("Unsupported Archaic Fix queue: " + writes + "/" + reads);
        replaceLong(method, (1L << 60) - 1, (1L << 61) - 1);
    }

    private static void patchBoundaries(MethodNode method, boolean hooks) {
        if (hooks && (method.name.equals("writeNeighborLightChecksToNBT")
            || method.name.equals("readNeighborLightChecksFromNBT"))) {
            method.instructions.clear();
            method.tryCatchBlocks.clear();
            if (method.localVariables != null) method.localVariables.clear();
            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
            method.instructions.add(
                new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    BRIDGE,
                    method.name.startsWith("write") ? "writeChecks" : "readChecks",
                    method.desc,
                    false));
            method.instructions.add(new InsnNode(Opcodes.RETURN));
            return;
        }
        method.desc = widenDescriptor(method.desc);
        for (AbstractInsnNode instruction = method.instructions.getFirst(); instruction != null;) {
            AbstractInsnNode next = instruction.getNext();
            if (instruction instanceof MethodInsnNode) {
                MethodInsnNode call = (MethodInsnNode) instruction;
                if (call.name.equals("getNeighborLightChecks") || call.name.equals("setNeighborLightChecks")) {
                    if (call.owner.equals(PREFIX + "lighting/api/IChunkLightingData")) {
                        call.owner = CHECKS;
                        call.name = call.name.startsWith("get") ? "hohenangst$getNeighborChecks"
                            : "hohenangst$setNeighborChecks";
                    }
                    call.desc = widenDescriptor(call.desc);
                } else if (call.name.equals("flagChunkBoundaryForUpdate")) call.desc = widenDescriptor(call.desc);
            } else if (instruction instanceof IntInsnNode && instruction.getOpcode() == Opcodes.NEWARRAY
                && ((IntInsnNode) instruction).operand == Opcodes.T_SHORT) {
                    ((IntInsnNode) instruction).operand = Opcodes.T_INT;
                } else if (instruction.getOpcode() == Opcodes.SALOAD)
                    method.instructions.set(instruction, new InsnNode(Opcodes.IALOAD));
            else if (instruction.getOpcode() == Opcodes.SASTORE)
                method.instructions.set(instruction, new InsnNode(Opcodes.IASTORE));
            else if (instruction.getOpcode() == Opcodes.I2S) method.instructions.remove(instruction);
            else if (instruction instanceof FrameNode) {
                widenFrame(((FrameNode) instruction).local);
                widenFrame(((FrameNode) instruction).stack);
            }
            instruction = next;
        }
        if (method.localVariables != null)
            for (LocalVariableNode local : method.localVariables) local.desc = widenDescriptor(local.desc);
        if (method.name.equals("scheduleRelightChecksForBoundary")) replaceInt(method, 16, 32);
        // Heights can equal 512. relightSkylightColumn accesses sections[yMin >> 4] only when yMin < 512.
        // Its callers pass differing heights; the smaller height therefore remains <= 511.
    }

    private static String widenDescriptor(String descriptor) {
        return descriptor.replace("[S", "[I")
            .replace(";S", ";I");
    }

    private static void widenFrame(List<Object> frame) {
        if (frame != null) for (int i = 0; i < frame.size(); i++) if ("[S".equals(frame.get(i))) frame.set(i, "[I");
    }

    private static void replaceLong(MethodNode method, long from, long to) {
        for (AbstractInsnNode instruction : method.instructions.toArray())
            if (instruction instanceof LdcInsnNode && Long.valueOf(from)
                .equals(((LdcInsnNode) instruction).cst)) ((LdcInsnNode) instruction).cst = to;
    }

    private static void replaceInt(MethodNode method, int from, int to) {
        for (AbstractInsnNode instruction : method.instructions.toArray())
            if (intConstant(instruction) == from) method.instructions.set(instruction, new LdcInsnNode(to));
    }

    private static int intConstant(AbstractInsnNode instruction) {
        if (instruction instanceof IntInsnNode && instruction.getOpcode() != Opcodes.NEWARRAY)
            return ((IntInsnNode) instruction).operand;
        if (instruction instanceof LdcInsnNode && ((LdcInsnNode) instruction).cst instanceof Integer)
            return (Integer) ((LdcInsnNode) instruction).cst;
        return Integer.MIN_VALUE;
    }

    private static void replaceRange(MethodNode method, AbstractInsnNode start, AbstractInsnNode end,
        InsnList replacement) {
        method.instructions.insertBefore(start, replacement);
        AbstractInsnNode after = end.getNext();
        while (start != after) {
            AbstractInsnNode next = start.getNext();
            method.instructions.remove(start);
            start = next;
        }
    }
}
