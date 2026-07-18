# Let There Be Portal Farms!

A Fabric mod for Minecraft 26.2+ for solving a common farming inconvenience - mobs despawning upon entering a nether portal, if no other players are nearby.

## Installation

This mod requires [Fabric API](https://modrinth.com/mod/fabric-api)!
Drop the mod .jar into your mods folder, and start the server!

## Why is this needed?

In vanilla Minecraft, if both a mob and a player exist in a world, and they're at least 128 blocks apart, then that mob despawns.
This a completely normal and vanilla feature, but here is where it gets annoying:

Say a despawnable mob enters a nether portal, and there is a player in the target dimension, nowhere close to that mob?
The mob immediately despawns.

This is what breaks most portal-based farms on multiplayer servers, making others rely on having a player AFK at the portal on the other side.

## What does this mod do?

LTBPF makes select mobs get a period of despawn immunity once they change dimensions.

If a whitelisted entity enters a portal, it gets a grace period in which they **cannot despawn**.
And once the period passes, their default despawning logic takes over - making them despawnable again.
It also does not touch entities or mobs which were already persistent by some means (*tamed, name tagged, etc.*), 
and checks if they were name tagged while in the grace period, to avoid taking away their persistence.

## Configuration

On first run, a config file is generated at `config/portal-fix-config.json`

This includes the entity whitelist, which entities get despawn immunity:
```
...
"minecraft:creeper",
"minecraft:spider",
"minecraft:zombified_piglin",
"minecraft:guardian",
"#minecraft:burn_in_daylight",
...and so on
```

I put in most farmable mobs I could think of, but if the default configuration doesn't suit you, then you can change it.

And an option called `persistenceDurationSeconds` for setting the length of the grace period in seconds. (default 60) 

### Commands / Permissions

/portalfix reload - reloads the configuration file

*requires the `portalfix.reload` permission or Operator privileges.*

### License

MIT — see [LICENSE](LICENSE).