## AlexStillTalking

A Discord bot that supports various War Thunder data mining features, such as creating jet thrust graphs, weapon
presets, flap rip speeds, and comparing game files from different game versions.

This project started as a way to improve my programming skills back in early 2022. The aim of it was to make a Discord
bot that would get War Thunder wiki pages and parse them to get the most useful information (like flap rip speeds,
repair/purchase costs, etc.) and display them as an embed. I had not realized yet how unreliable and just wrong the wiki
was, so with the help of some people who were knowledgeable about the
game's [datamines](https://github.com/gszabi99/War-Thunder-Datamine), this project began.

## Functionality

Currently, this bot can do the following:

* `/makethrustgraph`:
    * This command will display
      an [image](https://cdn.discordapp.com/attachments/720129756062023762/1152599344122441738/f_16c_block_5030_fuel_mig_29smt_9_1930_fuel_At_150_2.29.0.6.png)
      that shows the thrust and thrust to weight of a given plane(s) at different speeds. This command can compare up to
      8 different planes.
* `/maketdraggraph`:
    * This command will display
      an [image](https://cdn.discordapp.com/attachments/900137528995237928/1173754648356716644/f_16c_block_5030.0_fuel_jh_7a30.0_fuel_At_0_2.31.1.28.png)
      that shows drag of a given plane(s) at different speeds. This command can compare up to 8 different planes.
* `/maketdragthrustgraph`:
    * This command will display
      an [image](https://media.discordapp.net/attachments/900137528995237928/1173754989722738800/f_16c_block_5030.0_fuel_mig_29smt_9_1930.0_fuel_At_0_2.31.1.28.png)
      that shows the thrust, Drag and Acceleration of a given plane(s) at different speeds. This command can compare up
      to 8 different planes.
* `/lookup`:
    * plane: Displays a
      Discord [embed](https://cdn.discordapp.com/attachments/900137528995237928/1152593425502326835/image.png) of
      information about a certain plane.
    * Award: Displays a
      Discord [embed](https://cdn.discordapp.com/attachments/900137528995237928/1152594109522006137/image.png) about a
      certain award that you could get during a battle.
    * Weapon presets: Displays a
      Discord [message](https://cdn.discordapp.com/attachments/900137528995237928/1152595796693360640/image.png) with
      the weapon presets of that plane.
    * gun: Displays a
      Discord [message](https://cdn.discordapp.com/attachments/900137528995237928/1152596434315661342/image.png) with
      detailed information about the gun and its shells/belt presets.

* `/comparefms`, `/compareguns`, and `/comparemissiles`:
    * As you could guess from the name, you can compare different or the same flight model/gun/missile from different
      game versions, the result is sent as
      an [embed](https://cdn.discordapp.com/attachments/900137528995237928/1152606220860002344/image.png).

##

## Availability

The bot is currently available on these Discord servers:

### [Africa](https://discord.com/invite/YD6xUuh)

[Africa](https://discord.com/invite/YD6xUuh) is a community of War Thunder players that host and play custom battles.

### [Doofania](https://discord.gg/QxBVWEE)

[Doofania](https://discord.gg/QxBVWEE) is the Discord server of the War Thunder
YouTuber [DEFYN](https://www.youtube.com/@DEFYN).

After you join any of these servers, get any of the default roles and use the bot in the dedicated bots channel.

##

## Challenges

~~First, there are the features I failed to implement. The first is the wave drag calculation. From the datamined files,
there isn’t any clear way to do this.~~

Other than that, there’s the calculation of propeller thrust. This also seems impossible to do just from the game’s
files. Calculating the horsepower is really easy, but getting the prop efficiency was impossible just from the game’s
files.

I had hoped that by open-sourcing this project, interested people who are better at programming than I am could find
ways to solve these issues and make information more available to the War Thunder community. If these problems were to
be solved, then we could be able to compare plane performance objectively with no chance of user error that we would get
through manual testing.

### If you think that you could have found a way to solve any of these issues, you could either contact me on Discord (username: hadiiiiiiiiiiiiii) or create an issue on this repo.

#

Please note that this project was NOT intended to be of this scope, and thus the design is... to say the least,
disappointing. If I had the time, I would completely rewrite most of it.

