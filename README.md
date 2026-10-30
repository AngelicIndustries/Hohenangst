# Höhenangst

512-block world height for Minecraft 1.7.10: build from Y=0 through Y=511.
Existing terrain stays unchanged; the extra height starts as air.

[![hub](images/badges/github.png)](https://github.com/AngelicIndustries/Hohenangst/releases)
![forge](images/badges/forge.png)

<!-- [![curse](images/badges/curse.png)](https://www.curseforge.com/minecraft/mc-mods/hohenangst) -->
<!-- [![modrinth](images/badges/modrinth.png)](https://modrinth.com/mod/hohenangst) -->
<!-- [![67](images/badges/67.png)](https://67minecraft.com/mod/hohenangst) -->
<!-- [![maven](images/badges/maven.png)](https://maven.angelic.industries/#/releases/industries/angelic/hohenangst/Hohenangst) -->
<!-- mcmod.cn listing ID pending. -->

## Dependencies

Install the same Hohenangst version on **client and server**, with
[ChunkAPI 0.8.4](https://github.com/LegacyModdingMC/ChunkAPI),
[FalsePatternLib](https://github.com/FalsePattern/FalsePatternLib), and
[UniMixins](https://github.com/LegacyModdingMC/UniMixins).
Biomic is not required; its companion integration is deferred.

Planted saplings of Natura (2.8.19, 2.8.24) and HarvestCraft (1.3.13-GTNH) grow whole trees above Y=255.

Tested with Angelica + BSL shaders and the usual compatibility stack; see
[verification notes](docs/verification.md) for versions and limits.
Back up existing worlds before conversion. Removing the mod cannot preserve blocks above Y=255.

## Building

```sh
VERSION=0.0.1-dev ./gradlew build
```

## Credits

[GTNH ExampleMod](https://github.com/GTNewHorizons/ExampleMod1.7.10) for the build foundation.
[Cubic Chunks](https://github.com/GTNewHorizons/CubicChunks1710) for implementation reference.

## License

The code is licensed under [LGPLv3](LICENSE).

## Buy me some creatine

* [ko-fi.com](https://ko-fi.com/jackisasubtlejoke)
* Monero: `893tQ56jWt7czBsqAGPq8J5BDnYVCg2tvKpvwTcMY1LS79iDabopdxoUzNLEZtRTH4ewAcKLJ4DM4V41fvrJGHgeKArxwmJ`

![license](images/license_small.png)
