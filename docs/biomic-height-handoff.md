# Biomic height handoff — September 8, 2026

From the Biomic workspace, following jack's request to prepare for this companion.
This document is coordination, not authorization to deploy into existing worlds.
The Hohenangst implementation remains owned by its agent; no companion code was edited.

## Agreed bootstrap boundary

Read `docs/implementation.md`: Hohenangst adds buildable Y=256..511 while keeping
Biomic's existing generation at Y=0..255 and its schema-1 quart biome volume.
Biomic is preserving that contract. Installing the companion does **not** select
a new terrain transform. Upper space uses the column's surface biome fallback.

Biomic's new detached import infrastructure now has an explicit value type:

`industries.angelic.biomic.api.ChunkVerticalLayout`

- `LEGACY_256`, `EXTENDED_512`; `forHeight(int)` rejects other heights.
- `height()`, `sectionCount()`, `blockCount()`, `containsY(int)`.
- `index(x,y,z)` and `localX/Y/Z(index)` use `(x*16+z)*height+y`.
- `sectionMask(minimumY, maximumY)` uses **inclusive** clipped Y bounds and a
  long intermediate: all 32 sections are `-1`; section 31 is `0x80000000`.
- This type does NOT certify a live world's storage/network/render capabilities.
  No Hohenangst dependency, reflective autodetection or automatic profile change.

`TerraPackTerrainProjection(Envelope, ChunkVerticalLayout)` preserves the legacy
SkyLands/Origen compression or Tartarus identity mapping and adds air headroom.
Padding has no source provenance (`hasSourceAt` / `ProjectedChunk.hasSource`);
asking for its source Y throws instead of inventing an authored coordinate.
Default constructor remains 256. `TerraPackFeatureFit.begin` and
`TerraPackNativeMaterializer.begin` have explicit-layout overloads, while existing
signatures remain 256. These are detached preparations, not installed world chunks.

## Do not expand these merely because World#getHeight is 512

- `BiomeVolume.QUART_HEIGHT=64`, `CELL_COUNT=1024`, schema/packet version 1.
  Added `BLOCK_HEIGHT`, `getBlockHeight()`, `containsBlockY(int)` describe stored
  coverage, **not build height**. Save/packet bytes are unchanged.
- `BiomicBiomeApi` snapshot and authoritative lookup use coverage/surface fallback.
- `OrigenVerticalTransform`, `ModernVerticalTransform`, source density arrays,
  current providers and review signatures remain unchanged.
- The Worldbuilder API still rejects unsupported live world layouts. Do not remove
  this gate alone: its candidate bounds, sampler transforms and provenance must
  be made consistent first. Upper-space buildability is not terrain prediction.

## Hazards Hohenangst's runtime patches must cover

1. `PopulationBlockUpdateBatch`: range/full section masks, present sections, S21
   send path; `AuthoritativeLightReconciler` clamp and scan bounds. Existing
   `sectionMaskForRange(int,int)` has no World argument and is inherently legacy.
   Prefer per-world checks at callers rather than changing global constants.
2. `OrigenFeatureRuntime` population records use `short` local coordinates
   `x<<12 | z<<8 | y`. Never capture an upper-half block in this format. Repacking
   Y to nine bits requires a wider record and downstream delivery changes, not
   just replacing `255` with `511`. Current generators stay below 256.
3. Chunk storage, heightmap, tile/entity lists, S21/S26 masks, S22 and byte-height
   interaction packets, light updates and Angelica renderer bounds are companion
   work. A 131,072-cell detached buffer is not safe for a legacy Chunk constructor.
4. Treat masks as bitsets, not positive counts; `mask != 0`, not `mask > 0`.
5. Preserve legacy save access. Opening a tall save without the companion must
   fail safely before upper sections can be discarded. Both peers need an
   explicit compatible protocol. Record per-world effective storage height.

## Later Biomic full-height terrain profile (not implemented/selected)

Retaining full authored source height may be preferable to stretching compressed
terrain. It needs a separate saved generator/vertical-profile ID, explicit sea
level mapping, tall biome-volume codec negotiation, carver/feature bounds and
typed Worldbuilder support. Origen-only 256 remains available as the backup.
Do not silently upgrade an existing save or change its source-to-physical curve.

## Joint acceptance gate

Use new disposable worlds only. Independently verify existing lower-half terrain
and biome bytes, blocks/tiles/entities/light at 255/256/511, rejected 512,
section-31 packets, unload/reload, peer mismatch failure, safe tall-save rejection
without Hohenangst, and Angelica plus shaders. All servers `nogui`; all Biomic
graphical checks use `tools/run-isolated-client.sh` (bare xvfb-run is insufficient
when Wayland variables are inherited). Coordinate before any live review restart.

Biomic checks now pass: 33 Java tests, five Python tests and a real headless Forge
registry audit of three detached 131,072-cell columns (393,216 cells, no decoding
mismatches). Both pack columns preserve the previous 256-height lower half exactly;
the third is a synthetic non-air/metadata fixture at Y=255/256/511. Zero World chunks
were requested/loaded. This is **not a live 512-world proof**.
See `../Biomic/docs/hohenangst-height-preparation-sep08.md` for results and residuals.
