# Hohenangst → Biomic integration handoff — 2026-09-08

## Decision and current status

Jack explicitly prefers integration owned by Biomic. Hohenangst now contains **no
Biomic-targeted mixins, transformers, method-name matching, or runtime dependency**.
Biomic should adapt its own population, lighting and refresh code to the supported
world height. Do not add patches of Biomic back to Hohenangst.

This responds to [Biomic's incoming handoff](biomic-height-handoff.md). In particular,
its heading “Hazards Hohenangst's runtime patches must cover” no longer assigns
Biomic-owned code to this companion. The ownership decision above supersedes that
part of the earlier coordination note.

The standalone companion works at **Y=0..511** with the basic stack, Angelica and
BSL shaders. Joint Biomic runtime compatibility is **not yet verified**. The first
integration milestone preserves Biomic's current lower-half terrain and schema-1
biome volume, with buildable air above. Full-height terrain is a separate future
profile, not an automatic consequence of installing the companion.

Biomic source was inspected read-only at working-tree HEAD `07fdd28`; its working
tree is active, so locate the named methods rather than relying on line numbers.
No Biomic files were changed for this handoff.

## What Hohenangst supplies

| Property | Contract |
| --- | --- |
| Mod identity | `hohenangst`; same version required on both peers |
| Block coordinates | Y=0..511 inclusive; writes at -1 and 512 are rejected |
| Chunk storage | 32 ordinary 16³ sections and 32 entity lists |
| Heightmap values | Unmasked integer heights; **512 is valid**, representing first air above a block at 511 |
| Masks | 32-bit bitsets; section 31 is `0x80000000`, all sections are `-1` |
| Range masks | Inclusive, clipped bounds; reversed/outside ranges give zero |
| Relight completion counter | `chunk.getBlockStorageArray().length * 256`: 8192 here, 4096 in legacy chunks |
| Terrain / biomes | No terrain rescaling or high-altitude generation; no replacement biome-volume codec |
| Dependencies | ChunkAPI 0.8.4 and UniMixins, with FalsePatternLib support; optional EndlessIDs 1.7.4 tested |

`World#getHeight()` exposes the storage/build envelope. Do **not** substitute
`WorldProvider#getActualHeight()`: the Nether's legacy generation height can remain
128 while its chunk storage supports 512. Custom dimension/provider overrides
outside the tested stack still need validation.

Hohenangst owns vanilla/Forge storage and interaction bounds, ChunkAPI column
serialization, vanilla/EndlessIDs section codecs, Archaic Fix/Phosphor height
packing, Hodgepodge's tile bounds, and Angelica's section/render/light limits.
Biomic should not duplicate those patches or rewrite these internals.

## Integration without a Hohenangst compile dependency

Use Biomic's existing `ChunkVerticalLayout` and normal Minecraft/Forge accessors.
No new Hohenangst API, reflective bridge, or direct reference to its internal
`world.HeightRange` class is needed. Suggested Biomic-side selection policy:

```java
int height = world.getHeight();
if (height == 256) {
    return ChunkVerticalLayout.LEGACY_256;
}
if (height == 512 && Loader.isModLoaded("hohenangst")) {
    return ChunkVerticalLayout.EXTENDED_512;
}
throw new IllegalStateException("Unsupported live column height: " + height);
```

Resolve this after Forge mod discovery and against the **actual World**. When an
operation already has a Chunk, check that its storage length matches the selected
layout's section count. Do not load chunks merely to probe capabilities. Presence
plus these shape checks is an integration precondition, not proof of every mod's
compatibility; the joint runtime tests below provide that evidence.

Keep detached-buffer layout and live-world support as distinct concepts.
`ChunkVerticalLayout.EXTENDED_512` alone does not establish storage, networking,
lighting or rendering support. Do not make its existence select a generator.

## Biomic-owned changes to implement

### 1. Relight heightmap capture

In `mixins/early/minecraft/MixinChunk.java`, both
`biomic$capturePreviousColumnHeight` and `biomic$recordRelitColumn` currently read
`heightMap[...] & 255`. Hohenangst's former post-apply replacements of these
expressions have been **removed**. Biomic must retain the full stored height in
these helpers. **Do not replace 255 with 511**: that would turn heightmap value
512 into zero. Clip affected block ranges only when converting them into section
masks, using the world's layout.

### 2. Finalized-lighting completion and rebuild bounds

In `mixins/early/minecraft/MixinNetHandlerPlayClient.java`,
`biomic$prioritizeFinalizedLighting` currently sets `queuedLightChecks` to 4096.
The companion's former replacement has been **removed**. Biomic can derive the
completed-pass sentinel from `chunk.getBlockStorageArray().length * 256`, yielding
4096 or 8192 as appropriate.

Also update `client/FinalizedLightingRenderer.prioritizeChunkAndNeighbors`:
its `scheduleRebuildForBlockArea` call currently ends at Y=255. Pass the world or
explicit validated Y bounds from the caller and use an inclusive maximum of
`height - 1` (or the actual affected range). Preserve the existing horizontal
neighbor margin and optional-Angelica fallback. This helper was never patched by
Hohenangst; it needs explicit attention in addition to the removed hooks.

### 3. Population masks and partial S21 delivery

In `world/PopulationBlockUpdateBatch.java`:

- `sectionMaskForRange(int,int)` currently clips to 255 and uses `1 << count`.
  Add a layout/world-aware path and delegate to Biomic's existing long-intermediate
  `ChunkVerticalLayout.sectionMask`. Retain an explicitly legacy overload only
  where its callers really remain legacy; audit all live-world callers.
- `deferSectionRefresh` currently accepts only `blockY < 256`. Validate against
  the effective layout's block bounds before shifting.
- `presentSectionMask` already iterates `storage.length`; keep that correct path.
  `shouldPrioritizeSectionRefresh` already uses `mask != 0`; preserve it.
- Keep accumulated masks as `int` bitsets. Negative does not mean empty or invalid.
  Do not narrow them to shorts, mask with 65535, or use `mask > 0`.
- The existing `new S21PacketChunkData(chunk, false, sectionMask)` path can carry
  upper sections through Hohenangst's protocol. Supply the **full-width** mask.
  Keep delivery/population-settlement gates and tile-entity follow-up handling.

Reference cases for mask tests:

| Inclusive block range | Expected mask |
| --- | --- |
| 0..255 in a 512 layout | `0x0000ffff` |
| 255..256 | `0x00018000` |
| 496..511 | `0x80000000` |
| 0..511 | `0xffffffff` (`-1`) |
| 512..600, negative-only, or reversed | `0` |

### 4. Authoritative light reconciliation

In `world/AuthoritativeLightReconciler.java`, the top scan currently uses
`Math.min(255, chunk.getTopFilledSegment() + 15)`, and `LightAccess` clamps reads
with `Math.min(255, blockY)`. Derive the valid upper block bound from the selected
world layout while preserving the existing no-sky rules, neighbor-chunk policy,
settlement scheduling and work bounds. Do not let an out-of-range coordinate alias
a lower block. Exercise upper lamp removal as well as addition.

### 5. Packed population coordinates: retain the legacy boundary

`world/terrain/OrigenFeatureRuntime.java` still has `short` retained/tile-entity
positions packed as X4/Z4/Y8. Its `notifyWatchingClients` fallback scans 16 sections.
The first milestone keeps the existing generators below 256, so keep their legacy
representation explicitly bounded. Prevent any live upper-half write from entering
that short record. An upper update must use a full-coordinate path or a genuinely
wider record with corresponding downstream changes.

Hohenangst's ordinary `PlayerManager` hook sends upper updates as S23 packets plus
tile descriptions instead of putting them in vanilla's packed-short S22 list.
That does **not** repair a coordinate Biomic has already truncated, and S22 itself
is **not** extended. Likewise, do not use the legacy “one marker block per section”
strategy to assume that Hohenangst will batch the entire upper section: its upper
hook sends that one block immediately. Use explicit partial S21 refreshes when
whole-section contents/light must be delivered.

Do not globally replace 255/256/16 constants in Biomic. Many describe authored
terrain, palettes, horizontal chunk dimensions or legacy data formats.

## Serialization and save constraints

Use packet constructors and ChunkAPI normally; Biomic should not append a second
height extension. Hohenangst appends full masks to S21/S26 and full Y coordinates
to digging, placement and effect packets. For compatibility with vanilla full
chunk requests, S21 promotes `groundUp=true, mask=65535` to all 32 sections.
`groundUp=false, mask=65535` remains a lower-half partial refresh; use `-1` when
all 32 sections are intended. Constructor/codec handling accounts for absent
sections; do not call Hohenangst's internal `ColumnCodec` directly.

The ordinary `new Chunk(world, x, z)` allocates tall section/entity arrays. A tall
detached block buffer is **not** a supported drop-in replacement for legacy
array-taking Chunk constructors. Keep current terrain-array layouts for milestone
one. A later tall materializer should populate section storage explicitly and
preserve metadata, heightmaps, tile entities and lighting initialization.

ChunkAPI records data manager `hohenangst:height`, version `1`, in level.dat;
Hohenangst stores `Height=512` in its per-chunk manager data and stores widened
Phosphor boundary flags. Biomic's biome schema/bytes remain unchanged.

There is **no unconditional missing-companion save lock**. With ChunkAPI retained,
removing Hohenangst triggers a warning/backup confirmation before chunks load;
cancelling was verified to preserve region bytes. A user can override it, and
removing ChunkAPI too bypasses it. If Biomic requires an unbypassable rejection of
its tall worlds without the companion, Biomic must persist and enforce its own
required-storage-height marker before chunk access. Do not advertise safe downgrade
without that gate. Never silently discard upper sections.

Keep `BiomeVolume` coverage at 256, schema/packet version 1, and surface fallback
above it. Keep Origen/Modern transforms and generator IDs unchanged. Do not remove
Worldbuilder's unsupported-live-layout gate without making its bounds, transforms
and provenance consistent. Later full-height terrain needs a distinct saved profile.

## Validation and next acceptance gate

After removing the three Biomic-specific adaptations, on 2026-09-08:

- Build, Spotless, JUnit and Maven POM generation passed.
- A separate **obfuscated Java 8 dedicated server** passed the runtime audit:
  block/meta bounds, heightmap 512, path cache, upper chest contents, light
  propagation/removal, 32-bit S21/S26 round trips, interaction/effect packets,
  entity section assignment, and actual Anvil save/reload at 255/256/511.
- Source search found no remaining Biomic references in Hohenangst Java code.

The broader standalone client/shader, multiplayer and optional-EndlessIDs evidence
is in [verification.md](verification.md). Those earlier runs did not include Biomic;
they are not joint compatibility proof. No live server was restarted for this
cleanup. Its running JVM still contains the previously launched build.

Biomic's next gate should use disposable worlds and matching fresh builds on both
peers, with and without Hohenangst. Preserve the existing 256-height test baseline.
Then verify:

1. Lower-half blocks/metadata and biome codec bytes remain equivalent for the same
   saved generation profile and seed; no terrain/profile switch is selected.
2. Blocks, tile contents, entities and light at 255/256/511; reject 512; retain
   heightmap 512 when the top block is occupied.
3. Population-settled neighbors and upper edits trigger the correct **partial S21**
   and renderer refresh, including a **section-31-only** negative mask. Keep TE
   descriptions and reconnect/initial delivery working.
4. Upper light addition/removal, cross-chunk edges and final authoritative light
   remain correct after unload/reload and renderer/shader reload.
5. Save/reopen with both mods, peer mismatch rejection, and the chosen policy for
   a tall save reopened without the companion.
6. Angelica 2.2.9 + BSL v10.1.5 with the usual stack, then the Biomic-required stack
   including EndlessIDs. Check camera reconnect/reload above Y=511 as well.

Use Biomic's isolated graphical launch script and `nogui` servers. The current
Hohenangst review server at **127.0.0.1:6969** is jack's live diamond gallery with
Microsoft/Drasl authentication. Preserve it; run integration tests in a separate
working directory and port. Do not restart that server, repurpose its world, or
copy account credentials as part of the integration audit.

## Build artifacts and coordination

Current distributable: `../Hohenangst/build/libs/hohenangst-0.0.1-dev.jar`.
For a deobfuscated development dependency use the companion's matching `*-dev.jar`
variant (currently `hohenangst-0.0.1-dev-dev.jar`). Add it only to Biomic's optional
local/test runtime. No production hard dependency is needed for the height-aware
legacy path.

Rebuild with `VERSION=0.0.1-dev ./gradlew build`. Both peers must use the same version;
`NO-GIT-TAG-SET` is the plain-launch fallback in this uncommitted checkout, not a
unique compatibility fingerprint. Pin matching artifacts for the joint audit.
All changes remain uncommitted. Return the Biomic-side change list and actual joint
runtime results before considering the combined setup supported.
