[![Discord](https://img.shields.io/discord/899151012290498620.svg?label=discord&logo=discord)](https://discord.petrus.dev/)

<p align="center">
  <img src="icon/evernifecore.png" alt="EverNifeCore Logo" />
</p>

# EverNifeHyCore

> A comprehensive Java Framework for Hytale plugin development!

EverNifeHyCore is a powerful, feature-rich framework designed to accelerate Hytale plugin development. This project is a port of [EverNifeCore](https://github.com/EverNife/EverNifeCore) that was maded for Minecraft.

## 🚀 Quick Start

### For Server Owners
1. Download the latest JAR from [Releases](https://github.com/EverNife/EverNifeCore/releases)
2. Place in your `mods/` folder
3. Restart your server

### For Developers
```groovy
repositories {
    maven { url = 'https://maven.petrus.dev/public' }
}

dependencies {
    compileOnly 'br.com.finalcraft:EverNifeHyCore:2.0.5'
}
```

## 📋 Table of Contents

- [🌟 Key Features](#-key-features)
- [🏗️ Architecture](#️-architecture)
- [📚 Core Systems](#-core-systems)
  - [Command Framework](#command-framework)
  - [Configuration System](#configuration-system)
  - [FancyText & Messaging](#fancytext--messaging)
  - [GUI Framework](#gui-framework)
  - [Scheduler & Threading](#scheduler--threading)
  - [Player Data Management](#player-data-management)
  - [Database Integration](#database-integration)
  - [Localization System](#localization-system)
  - [Economy Integration](#economy-integration)
  - [Protection Systems](#protection-systems)
- [🔧 Utilities](#-utilities)
- [🔌 Integrations](#-integrations)
- [📖 Examples](#-examples)
- [🤝 Contributing](#-contributing)
- [📞 Support](#-support)

## 🌟 Key Features

### ⚡ **Developer Experience**
- **Smart Configuration Caching** with memory optimization
- **Annotation-driven** Command System

## 📚 Core Systems

### Command Framework

Powerful annotation-based command system with automatic argument parsing, permission handling, and localization.

```java
@FinalCMD(
    aliases = {"teleport", "tp"},
    permission = "myplugin.teleport",
    locales = {
        @FCLocale(lang = LocaleType.EN_US, text = "Teleport to a player or location")
    }
)
public void teleportCommand(
        CommandSender sender, 
        @Arg(name = "<player>") FPlayer target, // <> means notNull
        @Arg(name = "[destination]") FPlayer destination) { // [] means 'nullable'
    
    if (destination != null) {
        target.teleport(destination.getLocation());
        FancyText.of("§aTeleported §e" + target.getName() + " §ato §e" + destination.getName())
                .send(sender);
    }
}
```

**Features:**
- Automatic argument parsing and validation
- Built-in help system generation
- Permission and context validation
- Multi-language support
- Subcommand support

### Configuration System

Advanced YAML configuration with smart caching, comments, and type-safe access.

```java
// Basic usage
Config config = new Config(pluginInstance, "config.yml");

// Type-safe getters with defaults
String serverName = config.getOrSetDefaultValue("server.name", "MyServer", 
    "The display name of your server");

boolean enableFeature = config.getOrSetDefaultValue("features.teleport", true);
```

```java
// Complex objects with @Loadable/@Salvable
public class TeleportLocation implements Salvable {
  //var x, y, z, world;
  @Override
  public void onConfigSave(ConfigSection section) {
      section.setValue("world", this.world);
      section.setValue("x", this.x);
      section.setValue("y", this.y);
      section.setValue("z", this.z);
  }
  
  @Loadable @Salvable
  public static TeleportLocation onConfigLoad(ConfigSection section) {
      return new TeleportLocation(
          section.getString("world"),
          section.getDouble("x"),
          section.getDouble("y"),
          section.getDouble("z")
      );
  }
}

List<TeleportLocation> warps = config.getLoadableList("warps", TeleportLocation.class);
```

**Features:**
- Smart memory caching with automatic cleanup
- Comment preservation and generation
- Type-safe access methods
- Custom object serialization
- Async saving

### FancyText & Messaging

Rich text formatting with click/hover events and component chaining.

```java
// Simple usage
FancyText.of("§aClick here to teleport!")
    .send(fplayer);
```

### Player Data Management

Persistent player data with automatic saving and caching.

```java
public class MyPlayerData extends PlayerData {
    
    @Override
    public void onPlayerLogin() {
        // Called when player joins
    }
    
    public int getCoins() {
        return getPDSection().getOrSetDefaultValue("coins", 0);
    }
    
    public void addCoins(int amount) {
        int current = getCoins();
        getPDSection().setValue("coins", current + amount);
        markAsModified(); // Schedule async save
    }
}

// Usage
MyPlayerData data = PlayerController.getPlayerData(fPlayer, MyPlayerData.class);
data.addCoins(100);
```

### Localization System

Multi-language support with automatic message formatting.

```java
@FCLocale(lang = LocaleType.EN_US, text = "Welcome {player}!")
@FCLocale(lang = LocaleType.PT_BR, text = "Bem-vindo {player}!")
public static LocaleMessage WELCOME_MESSAGE;

// Usage
WELCOME_MESSAGE.send(fPlayer, "{player}", fPlayer.getName());
```

## 🔧 Utilities

### Reflection Utilities
```java
MethodInvoker method = FCReflectionUtil.getMethod(FPlayer.class, "getHandle");
Object nmsPlayer = method.invoke(fPlayer);
```

## 🔌 Integrations

- **Nothing yet** - But will have what is good in hytale!

## 📞 Support

- **Discord**: [Join our community](https://discord.petrus.dev/)
- **Issues**: [GitHub Issues](https://github.com/EverNife/EverNifeCore/issues)

---

<p align="center">
  <strong>Developed with ❤️ by <a href="https://github.com/EverNife">EverNife</a></strong>
  <br>
  <em>Empowering Hytale plugin development since it's launch at 13 of january of 2016</em>
</p>
