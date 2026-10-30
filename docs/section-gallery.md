# Diamond section gallery

The opt-in server fixture uses a separate void world named
`hohenangst-section-gallery`. It places four chunk columns, each with 32 diamond
wireframes covering the exact 16×16×16 section bounds from Y=0 through Y=511.
The columns have 32-block air gaps. Glowing three-digit labels inside each section
show its base block Y: 000, 016, …, 496. Labels face south, toward the landing deck.

A glass landing deck at Y=256 marks the old height boundary. Players enter in
creative flight at (0.5, 264, 64.5), facing the gallery. Daylight is fixed and mob
spawning disabled. The surrounding world remains empty, leaving clear sightlines
for renderer, lighting, distance and ceiling checks.

To recreate it, configure the dedicated server with:

```properties
level-name=hohenangst-section-gallery
level-type=FLAT
generator-settings=2;0;1;
generate-structures=false
max-build-height=512
gamemode=1
allow-flight=true
spawn-protection=0
```

Then launch `./gradlew runServer25 -PsectionGallery`. The builder refuses other
world names, saves the generated chunks, and records a marker inside the world
folder so later restarts preserve edits. The join positioning is limited to this
debug world and the opt-in launch. The exhibit consists entirely of vanilla blocks;
existing Hohenangst clients can reconnect without updating for this fixture.
