<h1 align="center">mscript</h1>

<h4 align="center">Script interpreter for Minecraft projects.</h4>

<p align="center">
    <a href="https://saic.one/discord">
        <img src="https://img.shields.io/discord/974288218839191612.svg?style=flat-square&label=discord&logo=discord&logoColor=white&color=7289da"/>
    </a>
    <a href="https://www.codefactor.io/repository/github/saicone/mscript">
        <img src="https://www.codefactor.io/repository/github/saicone/mscript/badge?style=flat-square"/>
    </a>
    <a href="https://docs.saicone.com/mscript/">
        <img src="https://img.shields.io/badge/Saicone-mscript%20Wiki-3b3bb0?logo=github&logoColor=white&style=flat-square"/>
    </a>
    <a href="https://javadoc.saicone.com/mscript/">
        <img src="https://img.shields.io/badge/JavaDoc-Online-green?style=flat-square"/>
    </a>
    <a href="https://central.sonatype.com/artifact/com.saicone/mscript">
        <img src="https://img.shields.io/maven-central/v/com.saicone/mscript-api"/>
    </a>
</p>

Multiplatform script interpreter for Minecraft projects in 3 steps.

Step 1: Configure.
```yaml
actions:
  - tell: '<green>Hello <cyan>%player_name%</cyan>!</green>'
  - tell: '&eYou can use &clegacy color &ecodes too!'
  - if: '%player_level% < 10'
    run:
      - console: 'xp give %player_name% 100'
      - title: '"<aqua>Take some XP!" "&fYou are now level &e%player_level%!" 20 70 10'
    else:
      - actionbar: '<red>You are not eligible for XP!</red>'
      - tell: '<red>Execution finished</red>'
      - return
  - tell: '<yellow>Lets wait for 5 seconds...</yellow>'
  - delay: 5 SECONDS
  - if:
      permission: 'lobby.go'
    run:
      - tell: '<yellow>And go the lobby!</yellow>'
      - server: lobby
```

Step 2: Compile.
```java
ConfigurationSection config = ...;

Execution execution = BukkitScripts.compile(config.get("actions"));
```

Step 3: Execute.
```java
Player player = ...;

execution.run(BukkitContext.of(player));
```

## Dependency

How to implement mscript library in your project.

This library contains the following artifacts:
* `mscript-api` - API for mscript library, contains all the interfaces and abstract classes.
* `mscript-platform-bukkit` - Platform implementation for bukkit-based projects (SpigotMC, PaperMC... etc).
* `mscript-platform-bungeecord` - Platform implementation for bungeecord-based projects (BungeeCord, Waterfall... etc).
* `mscript-platform-velocity` - Platform implementation for velocity-based projects.

build.gradle
```groovy
repositories {
    maven { url = uri('https://central.sonatype.com/repository/maven-snapshots') }
}

dependencies {
    implementation 'com.saicone:mscript-api:1.0-SNAPSHOT'
}
```

build.gradle.kts
```kotlin
repositories {
    maven("https://central.sonatype.com/repository/maven-snapshots")
}

dependencies {
    implementation("com.saicone:mscript-api:1.0-SNAPSHOT")
}
```

pom.xml
```xml
<dependencies>
    <dependency>
        <groupId>com.saicone</groupId>
        <artifactId>mscript-api</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```
