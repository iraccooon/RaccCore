# RaccCore

Custom core plugin for RaccCore servers. Built on Paper.

---

## Features

### RaccFurnaces
Bulk smelting — furnaces smelt entire stacks at once instead of one item at a time. Experience is scaled accordingly.

**Commands**
| Command | Description |
|--------|-------------|
| `/raccfurnaces toggle` | Toggle bulk smelting on/off |

**Permissions**
| Permission | Default | Description |
|-----------|---------|-------------|
| `raccfurnaces.admin` | false | Access to RaccFurnaces commands |
| `raccfurnaces.use` | true | Allows bulk smelting |

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
| `raccmaplock.admin` | false | Can lock/unlock any map regardless of owner |

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
A custom diamond shovel that converts concrete powder to concrete when broken. Comes with Efficiency V.

**Commands**
| Command | Description |
|--------|-------------|
| `/giveconcshovel` | Gives the player a Concrete Shovel |

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
  interval: "5min"   # supports min and sec (e.g. "30sec", "2min")
  messages:
    - "<gold>Welcome to the server!</gold>"
    - "<click:open_url:'https://discord.gg/example'><aqua>Join our Discord!</aqua></click>"
```

Messages cycle in order. Supports full MiniMessage formatting.

---

## Admin Commands

| Command | Description |
|--------|-------------|
| `/racc reload` | Reloads config.yml and restarts broadcasts |

**Permissions**
| Permission | Default | Description |
|-----------|---------|-------------|
| `racccore.admin` | false | Access to /racc reload |

---

## Configuration

Full `config.yml` example:

```yaml
# RaccFurnaces
RaccFurnaces-enabled: true

# Concrete Shovel durability (int or "false" for unbreakable)
concrete-shovel-durability: 1561

# Scheduled broadcasts
broadcasts:
  interval: "5min"
  messages:
    - "<gold>Welcome to the server!</gold>"
    - "<aqua>Join our Discord!</aqua>"
```

---

## Requirements
- Paper 1.21+
- Vault
- LuckPerms (recommended for permission management)
