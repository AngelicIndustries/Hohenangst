# Biomic joint runtime return — September 8, 2026

Biomic implemented its own layout/masks/lighting/refresh adaptations and a
required-storage-height save lock. No Biomic-targeted transformer was added here.
Details and exact retained fixtures:
`../Biomic/docs/hohenangst-joint-runtime-sep08.md`.

Joint dedicated-server fresh/reload passes:589,824 unchanged lower blocks and
identical schema1 biome volumes; upper log axes/chest/item entity, lamp addition/
removal, S21 section31/all masks and512 rejection. Independent Python reads the
saved Anvil data. Removing this companion from a disposable copy now fails at
Biomic world construction even with upstream warnings confirmed; region bytes
remain unchanged. This is NOT joint player-delivery or shader acceptance.

## Minimal companion fix made by the Biomic integration work

The strict fixture exposed a vanilla rebuild off-by-one. Place a log at511 in a
newly allocated section, then allocate another upper section: `generateSkylightMap`
starts its scan at `topSection+15` but samples `cursor-1`, ignoring row511 and
replacing heightmap512 with257. This is an engine issue, not a Biomic terrain rule.

Added `HeightRange.rebuildScanStart`, a first-store-only local4 adjustment in
`MixinChunk.generateSkylightMap`, and client-only `MixinClientChunkHeightmap` for
`generateHeightMap` with explicit early registration. Only upper-section scan
starts change. Lower-half rebuild behavior is preserved. The common hook passes
the real joint server fixture; client hook still needs runtime/graphical verification.
`HeightmapScanTest` adds pure helper checks; total3 tests pass here.

Local build command: `VERSION=0.0.1-biomic-height-fix ./gradlew test jar`.
Original pinned jar was retained. The new development jar was copied into the
isolated Biomic fixture only. **Gallery6969, authentication/config, user clients,
worlds and running snapshots were not touched. No commit or deployment.**

Remaining joint gate: matching Angelica/BSL client, actual partial section31 refresh,
TE descriptions, reconnect, cross-chunk upper light and missing-peer rejection.
Keep Biomic's Worldbuilder upper-layout guard until that service's own contract is
extended; keep generated terrain below256 in the first compatibility milestone.
