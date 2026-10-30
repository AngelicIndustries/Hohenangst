# 512-block implementation

Hohenangst provides Y=0..511 independently of Biomic. Terrain generation retains its
original layouts and height; upper space is buildable air. No vertical rescaling,
negative height, or new high-altitude terrain is implemented.

Chunks have 32 ordinary sections and entity lists. Anvil section Y bytes still fit
0..31. ChunkAPI's existing NBT managers persist sections; its per-section packet
codecs serialize full columns with 32-bit masks. Optional EndlessIDs codecs use the
same adapter. Section 31 and the full-column mask deliberately use the sign bit.
Cubic Chunks is an implementation reference, not a dependency.

Both peers require the same Hohenangst version. S21/S26 packets append full section
masks; digging, placement and effect packets append full Y coordinates. Upper block
updates use single-block packets to avoid vanilla's byte-height multi-block packing.
Tile entity descriptions retain their integer coordinates. Existing lower updates
keep their original path.

Early mixins expand world, chunk cache, placement, entity, lighting, server and
vanilla renderer limits. Late mixins adapt ChunkAPI/EndlessIDs codecs and Angelica's
renderer, world slice, section manager and coordinate clamp. Renderer startup
clamps its camera-section readiness lookup so reconnects and shader reloads above
the ceiling do not wait forever. World-slice overrides select both MCP and SRG
method names in the optional mod integration. Optional Poppeteer
queries can inspect the entire height. Shader terrain uses Angelica's normal mesh
pipeline; no shaderpack source changes are necessary.

ASM is limited to places ordinary mixins cannot safely address: merged ChunkAPI
and Hodgepodge methods, and Archaic Fix's packed lighting queue/boundary masks.
Phosphor positions retain 26-bit X/Z and gain 9-bit Y; light levels move to a parallel
queue. Boundary masks become 32-bit integers with a separate NBT key, including
legacy short-mask import. Critical instruction patterns fail fast if incompatible.
Hohenangst does not patch Biomic classes or methods contributed by Biomic mixins.
Biomic owns its optional integration using its layout descriptor and Minecraft's
height/storage accessors. See [the outgoing handoff](hohenangst-to-biomic-handoff.md).
Biomic itself has not been included in the standalone compatibility audit.

References inspected: GTNH CubicChunks1710 2a3f7bb, local Angelica 61b5301f,
ChunkAPI 0.8.4, EndlessIDs 1.7.4, Archaic Fix, and ../MINECRAFT_MOD_STYLE.md.
No upstream implementation source has been copied.
