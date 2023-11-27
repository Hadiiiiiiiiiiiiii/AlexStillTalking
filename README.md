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
      an [image](https://media.discordapp.net/attachments/900137528995237928/1175824040808689664/f_16c_block_5030.png)
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

####

## Availability

The bot is currently available on these Discord servers:

### [Doofania](https://discord.gg/QxBVWEE)

[Doofania](https://discord.gg/QxBVWEE) is the Discord server of the War Thunder
YouTuber [DEFYN](https://www.youtube.com/@DEFYN).

After you join any of these servers, get any of the default roles and use the bot in the dedicated bots channel.

If you want to add this bot to your own server, feel free to contact me.
####

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

### If you think that you could have found a way to solve any of these issues, you could create an issue on this repo.

#

Please note that this project was NOT intended to be of this scope, and thus the design is... to say the least,
disappointing. If I had the time, I would completely rewrite most of it.

## Using Data/Drag/drag.exe
This executable requires the .dll that is in the same folder. It expects a JSON as an argument, with variables like desired Angle of Attack (AOA), the plane's desired weight, whether the plane should try to adjust the AOA to maintain level flight, the height, and also the path to the blkx file containing the Flight Model file. Finally, the JSON should contain a JSON array with the name 'speed' that has the speeds for the drag calculation.

~~So far, most flight models are supported, but some that have inconclusive data aren't yet supported.~~

In case you’re using Windows, you can create a file named ‘input.json’ in the same directory. This file will be used if no system arguments are given.

An example of what an acceptable JSON might look like:
````
{"aoa":0,"weight":8807.8,"levelFlight":true,"speeds":[0,18,36,54,72,90,108,126,144,162,180,198,216,234,252,270,288,306,324,342,360,378,396,414,432,450,468,486,504,522,540,558,576,594,612,630,648,666,684,702,720,738,756,774,792,810,828,846,864,882,900,918,936,954,972,990,1008,1026,1044,1062,1080,1098,1116,1134,1152,1170,1188,1206,1224,1242,1260,1278,1296,1314,1332,1350,1368,1386,1404,1422,1440,1458,1476,1494,1512,1530,1548,1566,1584,1602,1620,1638,1656,1674,1692,1710,1728,1746,1764,1782,1800],"fmpath":"path/to/your/f_16aj.blkx","height":0}
````

You are free to use this executable as you would like. The source code for it is for now still not open source.