# PVP Toggle
[![Available on Modrith](https://img.shields.io/badge/available-on_modrinth-green)](https://modrinth.com/plugin/pvp-toggle)
![Modrinth Downloads](https://img.shields.io/modrinth/dt/XIb24zNt)
[![Discord](https://img.shields.io/discord/790676398602715208)](https://discord.gg/HAS9rmcWZF)

Let your players choose when they want to fight! When a player is protected, **they can't get damaged by other players** and also **can't damage other players themselves.**

Extensive configuration!

# Commands
 
| Command             | Use                                       | Permission                   | Aliases           |
|---------------------|-------------------------------------------|------------------------------|-------------------|
| `/pvp`              | Toggle pvp                                | `pvptoggle.pvp`              | N/A               |
| `/pvp <user>`       | Toggle pvp for user                       | `pvptoggle.pvp.others`       | N/A               |
| `/pvp reload`       | Reload the plugin                         | `pvptoggle.reload`           | N/A               |
| `/pvpstatus`        | Get if you are protected from pvp         | `pvptoggle.pvpstatus`        | `/pstatus`, `/ps` |
| `/pvpstatus <user>` | Get if someone else is protected from pvp | `pvptoggle.pvpstatus.others` | `/pstatus`, `/ps` |

# PlaceholderAPI Integration
This plugin supports **PlaceholderAPI**, and comes with the following placeholders.
- `%pvp-toggle_vulnerable%` Is the player vulnerable, expressed in *configurable* human-readable language.
- `%pvp-toggle_boolvulnerable%` Is the player vulnerable, expressed in `true` of `false`.

# Default config
```yml
# What should messages be prefixed with?
# You can use the formatting codes listed at https://minecraft.wiki/w/Formatting_codes#Color_codes
prefix: "§4PvP »"

# Should pvp be on by default?
default-pvp: false

# Do any worlds have PVPToggle disabled?
disabled-worlds:
#  - world

# What is the message sent to players in the chat if they try to use the pvp command while on cooldown?
# %s is replaced by the time remaining, in the format of "1 minute and 32 seconds" or "32 seconds"
cooldown-message: "%s of cooldown remaining."

# After enabling pvp, how many seconds must a player wait before disabling it?
cooldown: 120

# Should players be prevented from disabling pvp during a fight?
anti-abuse: true

# Should players be able to hit themselves?
hit-self: true

# Should particles be spawned when a player is hit, but protected?
particles: true

# Should a message be sent to a player when they hit another player, but fail because that player is protected?
feedback: true

# Should a player's pvp status be reset when they die?
death-status-reset: true

# Should pvp be turned on after a player dies? Doesn't apply if death-status-reset is false.
death-status: false

# How many seconds should a player's cooldown be set to when they die? -1 to disable.
death-cooldown: -1

# After how many seconds should a player's pvp be automatically re-enabled? -1 to disable.
limited-time: -1

# Should a player be sent a chat message when their pvp is automatically re-enabled?
limited-message: "You are now %s"

# Should a player be sent a chat message when they die?
death-message: "You are now %s"

# Should players be able to attack pets of protected players?
protect-pets: true

# Should players ever be able to hit their own pets? (More "friendlies" coming soon)
friendly-fire: false

# What should the message be when a player tries to attack someone but fails because they're protected?
# %s is replaced with the name of the attacked.
pvp-off-message: "You can't fight %s!"
# %r is replaced with the name of the pet, for example "Wolf" or "King George II."
pet-pvp-message: "You can't fight %s's %r!"

# What should the message be when you try to attack your own team, but fail because friendly-fire is false?
ff-message: "Friendly fire!"

# What should the message be when toggle pvp?
# %s is replaced with the new state, "Vulnerable" or "Protected" by default.
toggle-message: "You are now %s"

# What should the message be when the console tries to do something that only players can do?
console-message: "§cYou can't use this command from the console."

# What should the message be when someone tries to do something, but can't because they don't have permission to?
permission-message: "You don't have permission to use this command."

# What should the message be after reloading this plugin?
reload-message: "Reloaded!"

# What should the message be when the targeted player can't be found?
# %s is replaced with the name of the targeted player.
not-found-message: "%s isn't online."

# What should the message be after toggling another player's pvp?
# %s is replaced with the name of the targeted player.
# %r is replaced with the new state, "Vulnerable" or "Protected" by default.
remote-toggle-message: "%s is now %r"

# What should the message be for checking pvp status?
# %s is the name of the player
# %r is the state; "Vulnerable" or "Protected" by default. (See: `disabled` and `enabled`)
pvp-status-message: "Status of %s is now %r"

# What should the words for the two states of pvp be?
disabled: "§aProtected"
enabled: "§cVulnerable"
```

> pvp-toggle IS NOT AFFILIATED WITH Microsoft Corporation, Minecraft IS A REGISTERED TRADEMARK OF Microsoft Corporation
> 
> pvp-toggle IS NOT AFFILIATED WITH Rinth Inc, "Modrith" IS A REGISTERED TRADEMARK OF Rinth Inc
