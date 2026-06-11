# RaccCore

Custom core plugin for OracleMC servers. Built on Paper 26.1+.

**NOTE: RaccCore does not currently track or log any activity**

---

## Features

### RaccStacks
Stack-based item movement across furnaces, blast furnaces, smokers, droppers, and dispensers. Furnace-type blocks smelt entire stacks at once with scaled XP. Droppers and dispensers eject full stacks, with overflow dropped to the ground when the destination is full. Dispenser special-use items (buckets, arrows, eggs, potions, etc.) are unaffected.

**Commands**
| Command | Description |
|--------|-------------|
| `/raccstacks toggle` | Toggle all stack features on/off |
| `/raccstacks toggle furnaces` | Toggle stack smelting for furnaces |
| `/raccstacks toggle blastfurnaces` | Toggle stack smelting for blast furnaces |
| `/raccstacks toggle smokers` | Toggle stack smelting for smokers |
| `/raccstacks toggle droppers` | Toggle stack ejection for droppers |
| `/raccstacks toggle dispensers` | Toggle stack ejection for dispensers |
| `/raccstacks toggle all` | Toggle all stack features on/off |

**Permissions**
| Permission | Default | Description |
|-----------|---------|-------------|
| `raccstacks.admin` | false | Access to /raccstacks commands |

**Notes**
- Hoppers are handled natively via `hopper-amount` in `spigot.yml`
- Dispenser use-items (buckets, arrows, eggs, potions, armor, spawn eggs, etc.) retain vanilla behavior
- Dropper overflow that cannot fit in the destination inventory is dropped to the ground

---

### RaccLock (Map Locking)
Prevents map art theft by allowing players to lock their maps. Locked maps cannot be duplicated in crafting tables or cartography tables.

**Commands**
| Command | Description |
|--------|-------------|
| `/maplock lock` | Lock the map you are holding |
| `/maplock unlock` | Unlock the map you are holding (owner only) |

**Permissions**
| Permission | Default | Description |
|-----------|---------|-------------|
| `racclock.admin` | false | Can lock/unlock any map regardless of owner |

**Notes**
- Locking a map stamps it with "Created by: [player]" lore
- Lock ownership is stored in `maplocks.db`
- Admins unlocking another player's map will be shown the original owner's name

---

### RaccConcrete
Automatically converts concrete powder to concrete when dropped into water.

**Permissions**
| Permission | Default | Description |
|-----------|---------|-------------|
| `raccconcrete.use` | false | Allows concrete powder to convert in water |

---

### RaccConcreteShovel
A custom diamond shovel with Efficiency V that converts concrete powder to concrete when broken. Configurable durability.

**Commands**
| Command | Description |
|--------|-------------|
| `/giveconcshovel <player>` | Give a player the Concrete Shovel |

**Permissions**
| Permission | Default | Description |
|-----------|---------|-------------|
| `giveconcshovel.use` | false | Allows use of /giveconcshovel |

**Config**
```yaml
concrete-shovel-durability: 1561  # number of uses before breaking
                                   # set to "false" to make unbreakable
```

---

### RaccCast (Broadcasts)
Scheduled chat broadcasts with full MiniMessage support including colors, gradients, hover text, and clickable links.

**Config**
```yaml
broadcasts:
  interval: "10min"  # supports min and sec (e.g. "30sec", "2min")
  messages:
    - "<gold>Welcome to the server!</gold>"
    - "<click:open_url:'https://discord.gg/example'><aqua>Join our Discord!</aqua></click>"
```

Messages cycle in order. Supports full MiniMessage formatting. See https://webui.advntr.dev/ for easy formatting.

---

## Admin Commands

| Command | Description |
|--------|-------------|
| `/racc reload` | Reloads config.yml and restarts broadcasts |
| `/racc help` | Shows all available commands |

**Permissions**
| Permission | Default | Description |
|-----------|---------|-------------|
| `racccore.admin` | false | Access to /racc commands |

---

## Permissions Summary

| Permission | Default | Description |
|-----------|---------|-------------|
| `racccore.admin` | false | Access to /racc commands |
| `raccstacks.admin` | false | Access to /raccstacks commands |
| `racclock.admin` | false | Can lock/unlock any map regardless of owner |
| `giveconcshovel.use` | false | Allows use of /giveconcshovel |
| `raccconcrete.use` | false | Allows concrete powder to convert in water |

---

## Configuration

Full `config.yml` example:

```yaml
# RaccStacks
RaccFurnaces-enabled: false
RaccBlastFurnaces-enabled: false
RaccSmokers-enabled: false
RaccDroppers-enabled: false
RaccDispensers-enabled: false

# Concrete Shovel durability (int or "false" for unbreakable)
concrete-shovel-durability: 1561

# Scheduled broadcasts
broadcasts:
  interval: "10min"
  messages:
    - "<gold>Welcome to the server!</gold>"
    - "<aqua>Join our Discord!</aqua>"
```

---

## Requirements
- Paper 26.1+
- LuckPerms (recommended for permission management)