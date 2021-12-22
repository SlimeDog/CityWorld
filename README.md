### SlimeDog fork of CityWorld

DaddyChurchill always said: The great outdoors is all good and fine but sometimes you just want to go to town. Not any town will do, how about one that goes on forever or multiple ones separated by the largest mountains possible in Minecraft. But what is under those mountains? There is an entire world to explore, have fun!

But Daddy hasn't been to the city for a while now. We decided to moderize a few things.

#### Pending 5.1.0 Modifications 
- Add configurable lightpoles in world-specific configuration files
  - Defaults materials mirror previously-hardcoded values
    - LightPost_Height: 3
    - Materials_For_Lights: GLOWSTONE
    - Materials_For_LightPosts: SPRUCE_FENCE
    - Materials_For_LightPostBases: STONE
- Add `cityworld reload` command
  - Accessible from the console
  - Accessible in-game with  permission `cityworld.command.reload`
- Separate building floor materials from ceiling materials
  - `Materials_For_BuildingCeilings` will continue to populate the perimeter of each floor (every four levels)
  - `Materials_For_BuildingFloors` will populate the interior of each floor 
  - Configuration option `IncludeBuildingFloors`
    - Default `false` to maintain current behavior
  - Materials list
  ```
    Materials_For_BuildingFloors:
    - ACACIA_PLANKS
    - BIRCH_PLANKS
    - DARK_OAK_PLANKS
    - JUNGLE_PLANKS
    - OAK_PLANKS
    - SPRUCE_PLANKS
  ```
- Add bStats
- Update support for WorldEdit schematics and `citychunk regen` command

#### 5.0.0 Modifications to support MC 1.18.x
- Compile with Java 17
- Set api-version: 1.18
- Update materials
  - Material.GRASS_PATH -> Material.DIRT_PATH
- Update biomes
  - Biome.BIRCH_FOREST_HILLS -> Biome.BIRCH_FOREST
  - Biome.DESERT_HILLS -> Biome.DESERT
  - Biome.SNOWY_MOUNTAINS -> Biome.SNOWY_SLOPES
  - Biome.TAIGA_HILLS -> Biome.WINDSWEPT_HILLS

#### How to build it
- Download/install maven
- Download the source
- Unzip the source
  - `unzip CityWorld-master.zip` on Linux or MacOS
  - something similar on Windows
- Change directory to the source tree
  - `cd CityWorld-main` on Linux or MacOS
  - something similar on Windows
- Type `mvn package`
- Look in the `target` subdirectory for the JAR
