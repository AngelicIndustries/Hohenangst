# Standalone verification — 2026-09-08

Scope: Minecraft 1.7.10 / Forge 10.13.4.1614, Y=0..511, no Biomic. These are
disposable-world checks, not a certification of arbitrary generators or modpacks.

## Stack

| Component | Tested version |
| --- | --- |
| Angelica | 2.2.9 |
| NEI | 2.8.130-GTNH |
| Hodgepodge | 2.7.195 |
| Archaic Fix | 0.8.0 (earlier passes also used 0.7.7) |
| VintageResourcify / VRAP | 1.0.5 / 1.0.1 |
| Forgelin | 2.4.3-2.4.10 |
| AngelLib | 1.0.0 |
| BugTorch | 1.2.14 |
| Catalogue Vintage / ModularUI2 | 1.0.8-1.7.10 / 2.3.88-1.7.10 |
| ChunkAPI / FalsePatternLib | 0.8.4 / 1.12.2 |
| UniMixins | 0.3.1 in development; 0.2.1 in release-jar audits |
| Optional EndlessIDs | 1.7.4 |
| Shaderpack | BSL v10.1.5 |

ModularUI2 is needed by VintageResourcify's tooltip event integration in this stack.
Development-only mods are excluded from Hohenangst's published dependencies.

## Completed checks

- Gradle build, Spotless and JUnit section-mask boundary tests passed.
- A real dedicated Forge server passed world/server bounds, all 32 storage and
  entity-list allocations, block IDs and metadata at 255/256/300/495/496/511,
  rejected -1/512, heightmap value 512, upper chest inventory, upper light
  propagation/removal, and vertical path-hash separation.
- S21 and S26 encode/decode preserve all 32 mask bits. S21's decoded payload
  restores upper sections through the actual ChunkAPI codecs. Digging, placement
  and effect packets preserve Y=255/256/511 and consume exactly their payloads.
- Actual Anvil save/reload preserves upper blocks, metadata, chest contents and
  Phosphor's 32-bit deferred boundary-light flags.
- The dedicated audit also passed with optional EndlessIDs block/meta codecs.
- A release-jar, obfuscated Java 8 server passed the same audit with Archaic Fix,
  Hodgepodge, NEI, BugTorch, AngelLib and their supporting libraries. Entity
  section assignment and Anvil persistence passed at 255/256/511. A pathfinding
  cache reads upper blocks/metadata on the physical dedicated server; rendering
  methods stripped from that side have a separate client-only mixin. The same save
  reopened without an unnecessary conversion prompt.
- Poppeteer controlled an actual Angelica client with the complete development
  stack under isolated X11/Xvfb. Glowstone rendered and emitted light at Y=300.
  BSL loaded and reported active; rendered blocks were visually checked at
  Y=300 and Y=511. Normal use/attack placed and broke red wool at Y=511;
  authoritative server commands verified the result. Y=512 was rejected.
  Disconnect/reenter restored the upper blocks and metadata through initial
  chunk delivery.
- The complete release-jar Java 8 client loaded the same upper blocks and passed
  ordinary placement/breaking at Y=511. Reconnecting above the ceiling and
  reloading BSL exposed an Angelica startup wait on a nonexistent section; the
  camera-section clamp fixes both paths. No Archaic Fix skin mixin failure
  occurred in this production environment.
- A release client connected to a separate release dedicated server, received
  section-31 wool with its metadata in initial chunk delivery, placed and broke
  Y=511 blocks through normal interactions, and received authoritative success
  responses. The server rejected Y=512 and kept running with mob AI active.
- A client with Hohenangst rejected a server without it during the real FML
  handshake (`Rejecting connection SERVER: [FMLMod:hohenangst{0.0.1-dev}]`).
- Removing only Hohenangst from a copied tall save triggered ChunkAPI's missing
  `hohenangst:height` warning before chunk loading. Cancelling the warning left
  all 14 existing region files unchanged (SHA-256 comparison).

Client graphics checks used Mesa llvmpipe (OpenGL 4.6). This verifies rendering
correctness in that environment, not performance or every GPU/shaderpack.

## Reproduction

```sh
VERSION=0.0.1-dev ./gradlew build generatePomFileForMavenPublication
VERSION=0.0.1-dev ./gradlew runServer25 -PheightAudit
VERSION=0.0.1-dev ./gradlew runServer25 -PheightAudit -PwithEndlessIds
```

The audit requires a disposable server level named `hohenangst-height-audit*`,
`level-type=FLAT`, `max-build-height=512`, EULA acceptance and an unused loopback
port. It writes `run/server/height-audit-result.txt` and shuts down. Check that
report, not just Gradle's exit code. `-PheightAudit` confirms ChunkAPI's conversion
prompt only for this opt-in test; never use it on a personal save.

For client automation, build the sibling Poppeteer and pass its deobfuscated jar
with `-PpoppeteerJar=/absolute/path/to/poppeteer-dev.jar` to `runClient25`.
Unset `WAYLAND_DISPLAY` and set `XDG_SESSION_TYPE=x11` when launching with Xvfb.
`tools/poppeteer_rpc.py` authenticates to this project's loopback discovery file;
it never prints the discovery token. `-PheightClientAudit` confirms conversion
only for a disposable client run. Production audits stage release jars separately
because Gradle's development dependency substitutions are unsuitable for them.

## Limits and follow-up

Biomic's incoming handoff is in [biomic-height-handoff.md](biomic-height-handoff.md).
The [outgoing integration handoff](hohenangst-to-biomic-handoff.md) assigns Biomic's
height adaptations to Biomic itself. After removing the three provisional
Biomic-specific adaptations, the build and obfuscated dedicated-server runtime
audit passed again on 2026-09-08. Joint Biomic compatibility remains unverified;
no changes to its workspace were made. Vanilla terrain is not stretched.

ChunkAPI records the height data manager in level.dat and Hohenangst writes an
explicit 512-height marker per chunk. Keep both installed: ChunkAPI can prompt
and back up when Hohenangst is missing, but overriding that prompt or removing
ChunkAPI as well is not a safe downgrade path. There is no lossless conversion
to a 256-block world while upper blocks remain.

Archaic Fix logs a skin-memory-leak mixin mapping failure in the deobfuscated
client environment; it was absent in the release-jar Java 8 client. Height and
shader checks still completed. No unrelated
Archaic Fix option was disabled to conceal this failure.
