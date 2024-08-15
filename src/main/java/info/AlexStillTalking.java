package info;

import info.generate.Comparator;
import info.generate.MakeGuns;
import info.generate.MakeMissiles;
import info.templates.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AlexStillTalking extends ListenerAdapter {
    ArrayList<Gun> guns;

    WikiManager wiki = new WikiManager();
    PlanesManager planesManager = new PlanesManager();
    boolean spilly = true;
    boolean steve = true;
    String forzenLink = "https://media.discordapp.net/attachments/888435513516773456/1007324189658718258/caption.gif";
    List<Interaction> interactions = new ArrayList<>();
    Emoji shitter;
    public Role shitterRole;
    public List<Member> shitters;

    public AlexStillTalking(ArrayList<Gun> guns) {
        this.guns = guns;
        MakeMissiles.readMsls();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;
        if (shitter == null) {
            shitter = event.getJDA().getEmojiById(1172262540785483817L);
        }
        for (int i = 0; i < event.getMember().getRoles().size(); i++) {

            if (event.getMember().getRoles().get(i).getId().equals("1113561367107088404")) {
                event.getMessage().addReaction(shitter).queue(
                        success -> {
                        },
                        throwable -> {
                        }
                );

            }
        }

        if (event.getMember().getIdLong() == 164821597528653824L && event.getMessage().getAttachments().size() > 0) {
            event.getMessage().reply(forzenLink).queue();
        } else if (event.getGuild().getIdLong() == 692014646542073875L) {
            if (event.getAuthor().getIdLong() == 511766963651870732L) {
                if (Math.random() < 0.10 && event.getMessage().getAttachments().size() == 0) {
                    event.getMessage().delete().queue();
                }
            }
            if (event.getMessage().getContentRaw().equalsIgnoreCase("?spilly")) {
                if (spilly) {
                    event.getGuild().timeoutFor(event.getJDA().getUserById(511766963651870732L), 3, TimeUnit.SECONDS).queue(null, throwable -> {
                    });
                    event.getGuild().timeoutFor(event.getMember(), 4, TimeUnit.SECONDS)
                            .queue(null, throwable -> {
                            });
                }
                event.getMessage().delete().queueAfter(3500, TimeUnit.MILLISECONDS);
            }

            if (event.getMessage().getContentRaw().equalsIgnoreCase("?peepot")) {
                if (spilly) {
                    event.getGuild().timeoutFor(event.getJDA().getUserById(283042198122135554L), 3, TimeUnit.SECONDS).queue(null, throwable -> {
                    });
                    event.getGuild().timeoutFor(event.getMember(), 3, TimeUnit.SECONDS)
                            .queue(null, throwable -> {
                            });
                }
                event.getMessage().delete().queueAfter(3500, TimeUnit.MILLISECONDS);
            }
            if (event.getMessage().getContentRaw().equalsIgnoreCase("?porky")) {
                if (spilly) {
                    event.getGuild().timeoutFor(event.getJDA().getUserById(147474236653568000L), 3, TimeUnit.SECONDS).queue(null, throwable -> {
                    });
                    event.getGuild().timeoutFor(event.getMember(), 3, TimeUnit.SECONDS)
                            .queue(null, throwable -> {
                            });
                }
                event.getMessage().delete().queueAfter(3100, TimeUnit.MILLISECONDS);
            }

        } else if (event.getMessage().getContentRaw().equalsIgnoreCase("?disablespilly") && event.getMember().getIdLong() == 431138819698458626L) {
            spilly = !spilly;
        } else if (event.getMessage().getContentRaw().equalsIgnoreCase("?steve")) {
            if (steve) {
                try {
                    event.getGuild().timeoutFor(event.getGuild().getMemberById(469457071977267201L), 15, TimeUnit.SECONDS).queue(null, throwable -> {
                    });
                } catch (Exception ignored) {

                }
                try {

                    event.getGuild().timeoutFor(event.getGuild().getMemberById(1133977505678762085L), 15, TimeUnit.SECONDS).queue(null, throwable -> {
                    });
                } catch (Exception ignored) {

                }
                try {

                    event.getGuild().timeoutFor(event.getGuild().getMemberById(559853526503653386L), 15, TimeUnit.SECONDS).queue(null, throwable -> {
                    });
                } catch (Exception ignored) {

                }
                try {

                    event.getGuild().timeoutFor(event.getGuild().getMemberById(818572185114378300L), 15, TimeUnit.SECONDS).queue(null, throwable -> {
                    });
                } catch (Exception ignored) {

                }
                event.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (event.getName().equals("lookup") && event.getOption("plane") != null && event.getOption("award") == null && event.getOption("gun") == null) {
            try {
                if (!isLong(Objects.requireNonNull(event.getOption("plane")).getAsString()))
                    return;
                Plane p = planesManager.getPlane(event.getOption("plane").getAsLong());
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("sus").setColor(Color.BLACK).setThumbnail("attachment://plane.png").setFooter("game ver: " + Plane.gameVer + " for issues Message hadiiiiiiiiiiiiii")
                        .setDescription("``" + planesManager.getData(event.getOption("plane").getAsLong()) + "``").setTitle(p.name).build();
                try {
                    event.reply("").addFiles(FileUpload.fromData(new File("Data/graphs/" + p.actualName + "_" + p.alts.get(event.getOption("alt").getAsInt()) + ".png"), "graph.png"))
                            .addFiles(FileUpload.fromData(p.imgFile, "plane.png")).setEmbeds(embed.build()).queue();
                } catch (Exception e) {
                    try {
                        embed.setAuthor("sus").setColor(Color.BLACK).setThumbnail("attachment://plane.png").setFooter("game ver: " + Plane.gameVer + " for issues Message hadiiiiiiiiiiiiii")
                                .setDescription("```" + planesManager.getData(event.getOption("plane").getAsLong()) + "```").setTitle(p.name).build();
                        event.reply("").addFiles(FileUpload.fromData(p.imgFile, "plane.png")).setEmbeds(embed.build()).queue();
                    } catch (Exception eeee) {
                        embed.setAuthor("sus").setColor(Color.BLACK).setFooter("game ver: " + Plane.gameVer + " for issues Message hadiiiiiiiiiiiiii")
                                .setDescription("```" + planesManager.getData(event.getOption("plane").getAsLong()) + "```").setTitle(p.name).build();
                        event.reply("").setEmbeds(embed.build()).queue();
                    }
                }
            } catch (Exception e) {
                event.reply("something went wrong :( ").setEphemeral(true).queue();
                e.printStackTrace();
            }
        } else if (event.getName().equals("help")) {
            if (help.length() > 2000) {
                int splitIndex = help.lastIndexOf("\n\n", 2000);
                String helpPart1 = help.substring(0, splitIndex);
                String helpPart2 = help.substring(splitIndex + 1);

                event.reply(helpPart1).queue();
                event.getHook().sendMessage(helpPart2).queue();
            } else {
                event.reply(help).queue();
            }
        } else if (event.getName().equals("lookup") && event.getOption("plane") == null && event.getOption("award") == null && event.getOption("gun") == null && event.getOption("weaponpresets") != null) {
            try {
                if (!isLong(Objects.requireNonNull(event.getOption("weaponpresets")).getAsString()))
                    return;
                var loadouts = planesManager.getLoadouts(event.getOption("weaponpresets").getAsLong());
                if (loadouts != null) {
                    if (loadouts.length() < 2000)
                        event.reply("```" + planesManager.getLoadouts(event.getOption("weaponpresets").getAsLong()) + "```").queue();
                    else {
                        var split = loadouts.split("\n");
                        var p1 = new StringBuilder();
                        var p2 = new StringBuilder();
                        var p1d = false;
                        for (int i = 0; i < split.length; i++) {
                            if (p1.length() + split[i].length() < 1900) {
                                p1.append(split[i]).append("\n");
                            } else
                                p1d = true;
                            if (p1d) {
                                p2.append(split[i]).append("\n");
                            }
                        }
                        event.reply("```Message was split in two parts because its too long!\n" + p1 + "```").queue();
                        event.getChannel().sendMessage("```" + p2 + "```").queueAfter(1, TimeUnit.SECONDS);
                    }

                } else
                    event.reply("this Plane doesnt have a presets or isn't supported in the new preset format!").setEphemeral(true).queue();
            } catch (Exception e) {
                event.reply("this Plane doesnt have a presets or isn't supported in the new preset format!").setEphemeral(true).queue();
                System.out.println(e);
            }
        } else if (event.getName().equals("lookup") && event.getOption("plane") == null && event.getOption("award") != null && event.getOption("gun") == null) {
            try {

                Award a = wiki.getAward(event.getOption("award").getAsLong());
                EmbedBuilder embed = new EmbedBuilder();
                MessageEmbed m = embed.setColor(Color.BLUE).setDescription("```" + a.description + "```").setTitle(a.name).setThumbnail("https://wiki.warthunder.com" + a.imgLink).build();
                event.replyEmbeds(m).queue();
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (event.getName().equals("lookup") && event.getOption("gun") != null) {
            try {
                if (!isLong(Objects.requireNonNull(event.getOption("gun")).getAsString()))
                    return;
                for (int i = 0; i < guns.size(); i++) {
                    if (event.getOption("gun").getAsLong() == guns.get(i).id) {
                        event.reply("```" + guns.get(i).toString() + "```").queue();
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if (event.getName().equals("makethrustgraph") && event.getOption("plane1") != null && event.getOption("alt") != null) {
            event.deferReply().timeout(1, TimeUnit.MINUTES).setEphemeral(false).queue();

            List<Plane> planes = new ArrayList<>();
            List<Float> fuels = new ArrayList<>();

            for (int i = 1; i <= 7; i++) {
                if (event.getOption("plane" + i) != null) {
                    if (!isLong(Objects.requireNonNull(event.getOption("plane" + i)).getAsString()))
                        return;
                    Plane p = planesManager.getPlane(event.getOption("plane" + i).getAsLong());
                    if (!p.isactualJet) {
                        event.getHook().sendMessage("this plane does not have the standard jet thrust table. " + p.name).setEphemeral(true).queue();
                        return;
                    }
                    planes.add(p);
                    int fuel = 30;
                    if (event.getOption("fuel_" + i) != null)
                        fuel = event.getOption("fuel_" + i).getAsInt();
                    fuels.add((float) (fuel * 0.1));
                }
            }

            List<String> planeNames = planes.stream()
                    .map(p -> p.actualName)
                    .collect(Collectors.toList());

            List<String> duplicatePlaneNames = planeNames.stream()
                    .filter(p -> Collections.frequency(planeNames, p) > 1)
                    .distinct()
                    .collect(Collectors.toList());

            if (!duplicatePlaneNames.isEmpty()) {
                String message = duplicatePlaneNames.stream()
                        .collect(Collectors.joining(", "));

                event.getHook().sendMessage("Duplicate planes detected: " + message + " - A plane with the same flight model cannot be included twice!")
                        .queue();
                System.out.println("Duplicate planes detected: " + message + " - A plane with the same flight model cannot be included twice!");
                return;
            }

            int minspeed;
            int maxspeed;
            boolean invalidMaxSpeed = false;
            if (event.getOption("minspeed") != null && event.getOption("maxspeed") != null) {

                minspeed = event.getOption("minspeed").getAsInt();
                maxspeed = event.getOption("maxspeed").getAsInt();
                invalidMaxSpeed = planes.stream().anyMatch(p -> maxspeed < 0 || p.speedList.stream().max(Integer::compare).orElse(2000) < maxspeed || maxspeed < minspeed || maxspeed == minspeed);
            } else {
                minspeed = 0;
                maxspeed = 0;
            }
            if (invalidMaxSpeed) {
                event.getHook().sendMessage("Invalid Max Speed").setEphemeral(true).queue();
                return;
            }

            int alt = event.getOption("alt").getAsInt();
            StringBuilder errorMessage = new StringBuilder("Invalid options: ");

            if (alt < 0 || alt > 25000) {
                errorMessage.append("Altitude must be between 0 and 25000. ");
            }

            int maxAlt = planes.stream().mapToInt(p -> p.alts.stream().max(Integer::compare).orElse(20000)).max().orElse(20000);
            if (alt > maxAlt) {
                errorMessage.append("Altitude cannot be greater than the maximum altitude in the list (" + maxAlt + "). ");
            }

            boolean invalidFuel = fuels.stream().anyMatch(fuel -> fuel > 100 || fuel < 0);
            if (invalidFuel) {
                errorMessage.append("Fuel % must be between 0 and 100. ");
            }

            if (errorMessage.length() > "Invalid options: ".length()) {
                event.getHook().sendMessage(errorMessage.toString()).setEphemeral(true).queue();
                return;
            }
            Plane p1 = planes.get(0);
            String title = p1.actualName + "(" + fuels.get(0) * 10 + "% fuel)";
            for (int i = 1; i < planes.size(); i++) {
                Plane p = planes.get(i);
                title += " " + p.actualName + "(" + fuels.get(i) * 10 + "% fuel)";
            }

            ThrustGraph graph = new ThrustGraph(p1.speedList, title, "At " + alt, "Speed(TAS)", "Thrust(Kgf)", "OtherGraphs", planes, alt, fuels, minspeed, maxspeed);
            File file = graph.init();
            event.getHook().sendMessage("").setEphemeral(false).setFiles(FileUpload.fromData(file)).queue();
        } else if (event.getName().equals("makedraggraph") && event.getOption("plane1") != null && event.getOption("alt") != null) {
            event.deferReply().timeout(1, TimeUnit.MINUTES).setEphemeral(false).queue();

            List<Plane> planes = new ArrayList<>();
            List<Float> fuels = new ArrayList<>();

            for (int i = 1; i <= 7; i++) {
                if (event.getOption("plane" + i) != null) {
                    if (!isLong(Objects.requireNonNull(event.getOption("plane" + i)).getAsString()))
                        return;
                    Plane p = planesManager.getPlane(event.getOption("plane" + i).getAsLong());
                    // if (!p.newFm) {
                    //     event.getHook().sendMessage("This plane does have the standard wave drag tables! " + p.name).setEphemeral(true).queue();
                    //     return;
                    // }
                    planes.add(p);
                    int fuel = 30;
                    if (event.getOption("fuel_" + i) != null)
                        fuel = event.getOption("fuel_" + i).getAsInt();
                    fuels.add((float) (fuel * 0.1));
                }
            }

            List<String> planeNames = planes.stream()
                    .map(p -> p.actualName)
                    .toList();

            List<String> duplicatePlaneNames = planeNames.stream()
                    .filter(p -> Collections.frequency(planeNames, p) > 1)
                    .distinct()
                    .toList();

            if (!duplicatePlaneNames.isEmpty()) {
                String message = String.join(", ", duplicatePlaneNames);
                event.getHook().sendMessage("Duplicate planes detected: " + message + ". A plane with the same flight model cannot be included twice!").queue();
                System.out.println("Duplicate planes detected: " + message + ". A plane with the same flight model cannot be included twice!");
                return;
            }

            int alt = event.getOption("alt").getAsInt();

            int maxAlt = planes.stream().mapToInt(p -> p.alts.stream().max(Integer::compare).orElse(0)).max().orElse(0);
            System.out.println(maxAlt);
            if (alt < 0) {
                event.getHook().sendMessage("Invalid altitude! Altitude cannot Lower than 0").queue();
                return;
            }

            Plane p1 = planes.get(0);
            String title = p1.actualName + "(" + fuels.get(0) * 10 + "% fuel)";
            for (int i = 1; i < planes.size(); i++) {
                Plane p = planes.get(i);
                title += " " + p.actualName + "(" + fuels.get(i) * 10 + "% fuel)";
            }

            int minspeed = event.getOption("minspeed").getAsInt();
            int maxspeed = event.getOption("maxspeed").getAsInt();
            if (minspeed < 0) {
                event.getHook().sendMessage("Invalid minimum speed! Minimum speed cannot be less than 0. Your input was: " + minspeed).queue();
                return;
            }

            if (maxspeed < minspeed) {
                event.getHook().sendMessage("Invalid maximum speed! Maximum speed cannot be less than minimum speed. Your input was: " + maxspeed + " but it should be more than " + minspeed).queue();
                return;
            }

            if (maxspeed - minspeed < 3) {
                event.getHook().sendMessage("Invalid speed range! The difference between maximum and minimum speed must be at least 3. Your inputs were: minspeed = " + minspeed + ", maxspeed = " + maxspeed).queue();
                return;
            }

            double aoa = 0;
            boolean levelFlight = false;
            if (event.getOption("aoa") != null) {
                aoa = event.getOption("aoa").getAsDouble();
            } else {
                levelFlight = true;
            }
            ThrustGraph graph = new ThrustGraph(p1.speedList, title, "At " + alt, "Speed(TAS)", "Power (Kgf)", "OtherGraphs", planes, alt, fuels, minspeed, maxspeed, aoa, event.getHook(), levelFlight);
            File file = graph.init2();
            event.getHook().sendMessage("").setEphemeral(false).setFiles(FileUpload.fromData(file)).queue();
        } else if (event.getName().equals("makedragthrustgraph") && event.getOption("plane1") != null && event.getOption("alt") != null) {
            event.deferReply().timeout(1, TimeUnit.MINUTES).setEphemeral(false).queue();

            List<Plane> planes = new ArrayList<>();
            List<Float> fuels = new ArrayList<>();

            for (int i = 1; i <= 7; i++) {
                if (event.getOption("plane" + i) != null) {
                    if (!isLong(Objects.requireNonNull(event.getOption("plane" + i)).getAsString()))
                        return;
                    Plane p = planesManager.getPlane(event.getOption("plane" + i).getAsLong());
                    planes.add(p);
                    int fuel = 30;
                    if (event.getOption("fuel_" + i) != null)
                        if (event.getOption("fuel_" + i).getAsInt() > 0)
                            fuel = event.getOption("fuel_" + i).getAsInt();
                    fuels.add((float) (fuel * 0.1));
                }
            }

            List<String> planeNames = planes.stream()
                    .map(p -> p.actualName)
                    .toList();

            List<String> duplicatePlaneNames = planeNames.stream()
                    .filter(p -> Collections.frequency(planeNames, p) > 1)
                    .distinct()
                    .collect(Collectors.toList());

            if (!duplicatePlaneNames.isEmpty()) {
                String message = String.join(", ", duplicatePlaneNames);
                event.getHook().sendMessage("Duplicate planes detected: " + message + ". A plane with the same flight model cannot be included twice!").queue();
                return;
            }

            int minspeed = event.getOption("minspeed").getAsInt();
            int maxspeed = event.getOption("maxspeed").getAsInt();

            int minSpeedInPlanes = planes.stream().mapToInt(p -> p.speedList.stream().min(Integer::compare).orElse(0)).min().orElse(0);
            int maxSpeedInPlanes = planes.stream()
                    .mapToInt(p -> p.speedList.stream().max(Integer::compare).orElse(0))
                    .min().orElse(0);

            if (minspeed < minSpeedInPlanes || minspeed > maxSpeedInPlanes) {
                event.getHook().sendMessage("Invalid minimum speed! Minimum speed must be within the range of speeds in the planes. Your input was: " + minspeed + " but shouldnt be lower than " + minSpeedInPlanes).queue();
                return;
            }

            if (maxspeed < minSpeedInPlanes || maxspeed > maxSpeedInPlanes) {
                event.getHook().sendMessage("Invalid maximum speed! Maximum speed must be within the range of speeds in the planes. Your input was: " + maxspeed + " but shouldnt be higher than than " + maxSpeedInPlanes).queue();
                return;
            }

            int alt = event.getOption("alt").getAsInt();
            int maxAlt = planes.stream().mapToInt(p -> p.alts.stream().max(Integer::compare).orElse(0)).max().orElse(0);
            if (alt > maxAlt) {
                event.getHook().sendMessage("Invalid altitude! Altitude cannot be greater than the maximum altitude of the planes (" + maxAlt + "). Your input was: " + alt).queue();
                return;
            }

            boolean invalidFuel = fuels.stream().anyMatch(fuel -> fuel > 100 || fuel < 0);
            if (invalidFuel) {
                event.getHook().sendMessage("Invalid fuel! Fuel percentage must be between 0 and 100.").queue();
                return;
            }

            Plane p1 = planes.get(0);
            String title = p1.actualName + "(" + fuels.get(0) * 10 + "% fuel)";
            for (int i = 1; i < planes.size(); i++) {
                Plane p = planes.get(i);
                title += " " + p.actualName + "(" + fuels.get(i) * 10 + "% fuel)";
            }

            double aoa = 0;
            boolean levelFlight = false;
            if (event.getOption("aoa") != null) {
                aoa = event.getOption("aoa").getAsDouble();
            } else {
                levelFlight = true;
            }
            ThrustGraph graph = new ThrustGraph(p1.speedList, title, "At " + alt, "Speed(TAS)", "Power (Kgf)", "OtherGraphs", planes, alt, fuels, minspeed, maxspeed, aoa, event.getHook(), levelFlight);
            File file = graph.init3();
            event.getHook().sendMessage("").setEphemeral(false).setFiles(FileUpload.fromData(file)).queue();
        } else if (event.getName().equals("comparefms") && event.getOption("plane") != null && event.getOption("plane2") != null && event.getOption("gameversion1") != null && event.getOption("gameversion2") != null) {
            try {
                Integer.parseInt(event.getOption("plane").getAsString());
                Integer.parseInt(event.getOption("plane2").getAsString());
                Integer.parseInt(event.getOption("gameversion1").getAsString());
                Integer.parseInt(event.getOption("gameversion2").getAsString());
            } catch (Exception e) {
                event.reply("invalid options try again").setEphemeral(true).queue();
            }
            if (!isLong(Objects.requireNonNull(event.getOption("plane")).getAsString()))
                return;
            if (!isLong(Objects.requireNonNull(event.getOption("plane2")).getAsString()))
                return;
            event.deferReply().timeout(1, TimeUnit.MINUTES).queue();
            String gamever1 = Comparator.getGameVersion(event.getOption("gameversion1").getAsString());
            String gamever2 = Comparator.getGameVersion(event.getOption("gameversion2").getAsString());
            try {
                Plane p1 = planesManager.getPlane(event.getOption("plane").getAsLong());
                Plane p2 = planesManager.getPlane(event.getOption("plane2").getAsLong());

                new Thread(() ->
                {
                    var f = Comparator.compareAndSave(p1.actualName + ".blkx", p2.actualName + ".blkx", gamever1, gamever2, "FM");
                    if (f != null) {
                        Interaction i = new Interaction(event.getMember().getIdLong(), f.toPath().toString()
                                , p1.actualName, p2.actualName, gamever1, gamever2);
                        makeInteraction(event, i);
                    } else
                        event.reply("One or more of the files in the specified version werent found!").setEphemeral(true).queue();
                }).start();
            } catch (Exception e) {
                System.out.println(e);
                event.reply("some error, tell hadi fr").setEphemeral(true).queue();

            }
        } else if (event.getName().equals("compareguns") && event.getOption("gun") != null && event.getOption("gun2") != null && event.getOption("gameversion1") != null && event.getOption("gameversion2") != null) {
            try {
                Integer.parseInt(event.getOption("gun2").getAsString());
                Integer.parseInt(event.getOption("gun").getAsString());
                Integer.parseInt(event.getOption("gameversion1").getAsString());
                Integer.parseInt(event.getOption("gameversion2").getAsString());
            } catch (Exception e) {
                //   event.reply("invalid options try againe").setEphemeral(true).queue();
            }
            String gamever1 = Comparator.getGameVersion(event.getOption("gameversion1").getAsString());
            String gamever2 = Comparator.getGameVersion(event.getOption("gameversion2").getAsString());
            try {
                event.deferReply().timeout(1, TimeUnit.MINUTES).queue();
                Gun p1 = getGunById(event.getOption("gun").getAsLong());
                Gun p2 = getGunById(event.getOption("gun2").getAsLong());


                new Thread(() ->
                {
                    var f = Comparator.compareAndSave(p1.actualname + ".blkx", p2.actualname + ".blkx", gamever1, gamever2, "GUNS");
                    if (f != null) {
                        Interaction i = new Interaction(event.getMember().getIdLong(), f.toPath().toString()
                                , p1.actualname, p2.actualname, gamever1, gamever2);
                        makeInteraction(event, i);
                    } else
                        event.reply("One or more of the files in the specified version werent found!").setEphemeral(true).queue();

                }).start();
            } catch (Exception e) {
                System.out.println(e);
                event.reply("some error, tell hadi fr").setEphemeral(true).queue();

            }
        } else if (event.getName().equals("comparemissiles") && event.getOption("missile1") != null && event.getOption("missile2") != null && event.getOption("gameversion1") != null && event.getOption("gameversion2") != null) {
            try {
                Integer.parseInt(event.getOption("missile1").getAsString());
                Integer.parseInt(event.getOption("missile2").getAsString());
                Integer.parseInt(event.getOption("gameversion2").getAsString());
                Integer.parseInt(event.getOption("gameversion1").getAsString());
            } catch (Exception ignored) {
            }
            String gamever1 = Comparator.getGameVersion(event.getOption("gameversion1").getAsString());
            String gamever2 = Comparator.getGameVersion(event.getOption("gameversion2").getAsString());
            try {
                event.deferReply().timeout(1, TimeUnit.MINUTES).queue();
                Missile p1 = getMissileById(event.getOption("missile1").getAsLong());
                Missile p2 = getMissileById(event.getOption("missile2").getAsLong());


                new Thread(() ->
                {
                    var f = Comparator.compareAndSave(p1.getActualname() + ".blkx", p2.getActualname() + ".blkx", gamever1, gamever2, "MISSILES");
                    if (f != null) {
                        Interaction i = new Interaction(event.getMember().getIdLong(), f.toPath().toString()
                                , p1.getActualname(), p2.getActualname(), gamever1, gamever2);
                        makeInteraction(event, i);
                    } else
                        event.reply("One or more of the files in the specified version werent found!").setEphemeral(true).queue();


                }).start();
            } catch (Exception e) {
                System.out.println(e);
                event.reply("some error, tell hadi fr").setEphemeral(true).queue();

            }
        }
        else if (event.getName().equals("getthrust") && event.getOption("plane") != null && event.getOption("alt") != null && event.getOption("speed") != null && event.getOption("weight") != null) {
            event.deferReply().timeout(1, TimeUnit.MINUTES).setEphemeral(false).queue();

            if (!isLong(Objects.requireNonNull(event.getOption("plane")).getAsString())) {
                event.getHook().sendMessage("Invalid plane ID. Please provide a valid long value.").queue();
                return;
            }

            Plane plane = planesManager.getPlane(event.getOption("plane").getAsLong());
            if (plane == null) {
                event.getHook().sendMessage("Plane not found. Please provide a valid plane ID.").queue();
                return;
            }

            int speed = event.getOption("speed").getAsInt();
            int minSpeed = plane.speedList.stream().min(Integer::compare).orElse(0);
            int maxSpeed = plane.speedList.stream().max(Integer::compare).orElse(0);

            if (speed < minSpeed || speed > maxSpeed) {
                event.getHook().sendMessage("Invalid speed! Speed must be within the range of the plane's speeds. Your input was: " + speed + ", but it should be between " + minSpeed + " and " + maxSpeed).queue();
                return;
            }

            int alt = event.getOption("alt").getAsInt();
            int maxAlt = plane.alts.stream().max(Integer::compare).orElse(0);
            if (alt > maxAlt) {
                event.getHook().sendMessage("Invalid altitude! Altitude cannot be greater than the maximum altitude of the plane (" + maxAlt + "). Your input was: " + alt).queue();
                return;
            }

            double weight = 0;
            if (event.getOption("weight") != null) {
                weight = event.getOption("weight").getAsDouble();
            }

            double thrust = 0;
            if (plane.thrusts2 == null) {
                thrust = plane.getThrust(alt, speed, plane.thrusts);
            } else {
                thrust = plane.getThrust(alt, speed, plane.thrusts);
                thrust += plane.getThrust(alt, speed, plane.thrusts2);
            }
            double thrustToWeightRatio = weight > 0 ? thrust / weight : 0;

            String response = String.format("```\nPlane: %s\nSpeed: %d km/h\nThrust: %.2f kgf\nWeight: %.2f kg\nThrust-to-Weight Ratio: %.4f```",
                    plane.name, speed, thrust, weight, thrustToWeightRatio);

            event.getHook().sendMessage(response).queue();
        }
        else {
            System.out.println("event.getName() "+event.getName());
            System.out.println("event.getNameasd "+event.getOption("alt"));
            System.out.println("event.getOption(\"plane\") "+event.getOption("plane"));
            event.reply("invalid options :(").setEphemeral(true).queue();
        }
    }

    private boolean isLong(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private void makeInteraction(SlashCommandInteractionEvent event, Interaction i) {
        if (i.splitFile.size() > 1) {
            var e = event.getHook().sendMessage("").setEmbeds(i.showFirstPage().build()).complete();
            i.msgId = e.getIdLong();
            i.msg = e;
            e.addReaction(Emoji.fromUnicode("⏮️")).queue();
            e.addReaction(Emoji.fromUnicode("⬅️")).queue();
            e.addReaction(Emoji.fromUnicode("➡️")).queue();
            e.addReaction(Emoji.fromUnicode("⏭️")).queue();
            interactions.add(i);
        } else
            event.getHook().sendMessage("there is no difference between " + i.getPlane1() + " on game version " + i.getGamever1() + " and " + i.getPlane2() + " on game version " + i.getGamever2() + ".").queue();
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot())
            return;
        var b = false;
        Interaction interaction = null;
        for (int i = 0; i < interactions.size(); i++) {
            if (interactions.get(i).msgId == event.getMessageIdLong()) {
                interaction = interactions.get(i);
                b = true;
            }
        }
        if (b) {
            if (interaction.getAuthorid() == event.retrieveMember().complete().getIdLong()) {
                if (event.getEmoji().asUnicode().getName().equals("⏮️")) {
                    EmbedBuilder bb = interaction.showFirstPage();
                    if (bb != null)
                        interaction.msg.editMessageEmbeds(bb.build()).queue();
                } else if (event.getEmoji().asUnicode().getName().equals("⬅️")) {
                    EmbedBuilder bb = interaction.getPrevPage();
                    if (bb != null)
                        interaction.msg.editMessageEmbeds(bb.build()).queue();
                } else if (event.getEmoji().asUnicode().getName().equals("➡️")) {
                    EmbedBuilder bb = interaction.getNextPage();
                    if (bb != null)
                        interaction.msg.editMessageEmbeds(bb.build()).queue();
                } else if (event.getEmoji().asUnicode().getName().equals("⏭️")) {
                    EmbedBuilder bb = interaction.showLastPage();
                    if (bb != null)
                        interaction.msg.editMessageEmbeds(bb.build()).queue();
                }
            }
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        // System.out.println(event.getEmoji().asUnicode().getName());

        var b = false;
        Interaction interaction = null;
        for (int i = 0; i < interactions.size(); i++) {
            if (interactions.get(i).msgId == event.getMessageIdLong()) {
                interaction = interactions.get(i);
                b = true;
            }
        }
        if (b) {
            if (interaction.getAuthorid() == event.retrieveMember().complete().getIdLong()) {
                if (event.getEmoji().asUnicode().getName().equals("⏮️")) {
                    EmbedBuilder bb = interaction.showFirstPage();
                    if (bb != null)
                        interaction.msg.editMessageEmbeds(bb.build()).queue();
                } else if (event.getEmoji().asUnicode().getName().equals("⬅️")) {
                    EmbedBuilder bb = interaction.getPrevPage();
                    if (bb != null)
                        interaction.msg.editMessageEmbeds(bb.build()).queue();
                } else if (event.getEmoji().asUnicode().getName().equals("➡️")) {
                    EmbedBuilder bb = interaction.getNextPage();
                    if (bb != null)
                        interaction.msg.editMessageEmbeds(bb.build()).queue();
                } else if (event.getEmoji().asUnicode().getName().equals("⏭️")) {
                    EmbedBuilder bb = interaction.showLastPage();
                    if (bb != null)
                        interaction.msg.editMessageEmbeds(bb.build()).queue();
                }
            }
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {

        if (event.getName().equals("lookup") && event.getFocusedOption().getName().equals("plane") || event.getName().equals("lookup") && event.getFocusedOption().getName().equals("weaponpresets")) {
            event.replyChoices(planeChoicesLookup(event.getFocusedOption().getValue().toLowerCase())).queue();
            return;
        }
        if (event.getName().equals("lookup") && event.getFocusedOption().getName().equals("award")) {
            if (wiki.awards.size() != 0)
                event.replyChoices(awardChoices(event.getFocusedOption().getValue().toLowerCase())).queue();
        }
        if (event.getName().equals("lookup") && event.getFocusedOption().getName().equals("gun")) {
            event.replyChoices(guns(event.getFocusedOption().getValue().toLowerCase())).queue();
        }
        if (event.getName().equals("lookup") && event.getFocusedOption().getName().equals("alt")) {
            if (event.getOption("plane") != null)
                if (planesManager.getPlane(event.getOption("plane").getAsLong()) != null)
                    event.replyChoices(alts(event.getFocusedOption().getValue().toLowerCase(), planesManager.getPlane(event.getOption("plane").getAsLong()))).queue();
        }

        if (event.getName().equals("makethrustgraph") && event.getFocusedOption().getName().contains("plane") || event.getName().equals("getthrust") && event.getFocusedOption().getName().contains("plane") ) {

            event.replyChoices(planeChoicesThrustGraph(event.getFocusedOption().getValue().toLowerCase())).queue();
        }
        if (event.getName().equals("makedraggraph") && event.getFocusedOption().getName().contains("plane")) {

            event.replyChoices(planeChoicesDrag(event.getFocusedOption().getValue().toLowerCase())).queue();
        }
        if (event.getName().equals("makedragthrustgraph") && event.getFocusedOption().getName().contains("plane")) {

            event.replyChoices(planeChoicesDragThrust(event.getFocusedOption().getValue().toLowerCase())).queue();
        }
        if (event.getName().equals("comparefms") && event.getFocusedOption().getName().equals("plane") || event.getName().equals("comparefms") && event.getFocusedOption().getName().equals("plane2")) {
            event.replyChoices(planeChoicesLookup(event.getFocusedOption().getValue().toLowerCase())).queue();
        }
        if (event.getFocusedOption().getName().equals("gameversion1") || event.getFocusedOption().getName().equals("gameversion2")) {
            event.replyChoices(gameVersions(event.getFocusedOption().getValue())).queue();
        }
        if (event.getName().equals("compareguns") && event.getFocusedOption().getName().equals("gun") || event.getName().equals("compareguns") && event.getFocusedOption().getName().equals("gun2")) {
            event.replyChoices(guns(event.getFocusedOption().getValue().toLowerCase())).queue();
        }
        if (event.getName().equals("comparemissiles") && event.getFocusedOption().getName().equals("missile1") || event.getName().equals("comparemissiles") && event.getFocusedOption().getName().equals("missile2")) {
            event.replyChoices(missileChoices(event.getFocusedOption().getValue().toLowerCase())).queue();
        }

    }


    private List<Command.Choice> planeChoicesLookup(String s) {
        s = s.toLowerCase();
        var finalS = s;
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

        java.util.Comparator<Plane> comparator = java.util.Comparator
                .comparing((Plane plane) -> !plane.name.toLowerCase().contains(finalS))
                .thenComparingInt(plane -> levenshteinDistance.apply(plane.name.toLowerCase(), finalS));

        return planesManager.planes.stream()
                .sorted(comparator)
                .map(u -> new Command.Choice(u.name + "(" + u.actualName + ")", u.uid))
                .limit(25)
                .collect(Collectors.toList());
    }


    private List<Command.Choice> planeChoicesThrustGraph(String s) {
        s = s.toLowerCase();
        var finalS = s;
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

        java.util.Comparator<Plane> comparator = java.util.Comparator
                .comparing((Plane plane) -> !plane.name.toLowerCase().contains(finalS))
                .thenComparingInt(plane -> levenshteinDistance.apply(plane.name.toLowerCase(), finalS));

        return planesManager.planes.stream()
                .filter(plane -> plane.isactualJet)
                .sorted(comparator)
                .map(u -> new Command.Choice(u.name, u.uid))
                .limit(25)
                .collect(Collectors.toList());
    }


    private List<Command.Choice> planeChoicesDragThrust(String s) {
        s = s.toLowerCase();
        var finalS = s;
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

        java.util.Comparator<Plane> comparator = java.util.Comparator
                .comparing((Plane plane) -> !plane.name.toLowerCase().contains(finalS))
                .thenComparingInt(plane -> levenshteinDistance.apply(plane.name.toLowerCase(), finalS));

        return planesManager.planes.stream()
                //     .filter(plane -> plane.cxcheck)
                .filter(plane -> plane.isactualJet)
                .sorted(comparator)
                .map(u -> new Command.Choice(u.name, u.uid))
                .limit(25)
                .collect(Collectors.toList());
    }

    private List<Command.Choice> planeChoicesDrag(String s) {
        s = s.toLowerCase();
        var finalS = s;
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

        java.util.Comparator<Plane> comparator = java.util.Comparator
                .comparing((Plane plane) -> !plane.name.toLowerCase().contains(finalS))
                .thenComparingInt(plane -> levenshteinDistance.apply(plane.name.toLowerCase(), finalS));

        return planesManager.planes.stream()
                // .filter(plane -> plane.cxcheck)
                .sorted(comparator)
                .map(u -> new Command.Choice(u.name, u.uid))
                .limit(25)
                .collect(Collectors.toList());
    }

    private List<Command.Choice> awardChoices(String s) {
        s = s.toLowerCase();
        var finalS = s;
        return wiki.awards.stream().filter(u -> u.name.toLowerCase().contains(finalS) || u.name.equalsIgnoreCase(finalS)).map(u -> new Command.Choice(u.name, u.ID)).limit(25).collect(Collectors.toList());
    }


    private List<Command.Choice> guns(String s) {
        s = s.toLowerCase();
        var finalS = s;
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

        java.util.Comparator<Gun> comparator = java.util.Comparator
                .comparing((Gun gun) -> !gun.name.toLowerCase().contains(finalS))
                .thenComparingInt(gun -> levenshteinDistance.apply(gun.name.toLowerCase(), finalS));

        return guns.stream()
                .sorted(comparator)
                .map(u -> new Command.Choice(u.name, u.id))
                .limit(25)
                .collect(Collectors.toList());
    }

    private List<Command.Choice> gameVersions(String s) {
        s = s.toLowerCase();
        var finalS = s;
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

        java.util.Comparator<Commit> comparator = java.util.Comparator
                .comparing((Commit commit) -> !commit.commit.message.toLowerCase().contains(finalS))
                .thenComparingInt(commit -> levenshteinDistance.apply(commit.commit.message.toLowerCase(), finalS));

        if (s.isEmpty()) {
            comparator = java.util.Comparator.comparing((Commit commit) -> commit.commit.committer.date, java.util.Comparator.reverseOrder());
        }

        return Comparator.commits.stream()
                .sorted(comparator)
                .map(u -> new Command.Choice(u.commit.message + "(" + u.commit.committer.date + ")", u.commit.message.replace(".", "")))
                .limit(25)
                .collect(Collectors.toList());
    }


    private List<Command.Choice> missileChoices(String s) {
        s = s.toLowerCase();
        var finalS = s;
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

        java.util.Comparator<Missile> comparator = java.util.Comparator
                .comparing((Missile missile) -> !missile.getName().toLowerCase().contains(finalS))
                .thenComparingInt(missile -> levenshteinDistance.apply(missile.getName().toLowerCase(), finalS));

        return MakeMissiles.missiles.stream()
                .sorted(comparator)
                .map(u -> new Command.Choice(u.getName() + "(" + u.getActualname() + ")", u.getId()))
                .limit(25)
                .collect(Collectors.toList());
    }


    private List<Command.Choice> alts(String s, Plane plane) {
        List<Command.Choice> alts = new ArrayList<>();
        s = s.toLowerCase();
        for (int i = 0; i < plane.alts.size(); i++) {
            alts.add(new Command.Choice(String.valueOf(plane.alts.get(i)), i));
        }
        return alts;
    }

    private Gun getGunById(long uid) {
        for (int i = 0; i < guns.size(); i++) {
            if (uid == guns.get(i).id) {
                return guns.get(i);
            }
        }
        return null;
    }

    private Missile getMissileById(long uid) {
        for (int i = 0; i < MakeMissiles.missiles.size(); i++) {
            if (uid == MakeMissiles.missiles.get(i).getId()) {
                return MakeMissiles.missiles.get(i);
            }
        }
        return null;
    }


    public void setApi(JDA api) {
        try {
            api.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {

        event.getGuild().upsertCommand(Commands.slash("help", "Get help on how to use this bot.")
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
        ).queue();

        event.getGuild().upsertCommand(Commands.slash("lookup", "Search for information about guns, planes, awards, and weapon presets.")
                .addOptions(new OptionData(OptionType.INTEGER, "plane", "Enter the name of the plane you want to search for.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.STRING, "award", "Enter the name of the award you want to search for.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "weaponpresets", "Enter the name of the plane to view its weapon presets.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "gun", "Enter the name of the gun you want to search for.").setAutoComplete(true).setRequired(false)
                ).setDefaultPermissions(DefaultMemberPermissions.ENABLED)
        ).queue();

        event.getGuild().upsertCommand(Commands.slash("makethrustgraph", "Create a thrust graph for a specific altitude.")
                .addOptions(
                        new OptionData(OptionType.INTEGER, "plane1", "Enter the name of the plane to create a thrust graph for.").setAutoComplete(true).setRequired(true),
                        new OptionData(OptionType.INTEGER, "alt", "Enter the altitude at which you want to create the graph.").setRequired(true),
                        new OptionData(OptionType.INTEGER, "minspeed", "Enter the minimum speed for the thrust graph.").setRequired(false),
                        new OptionData(OptionType.INTEGER, "maxspeed", "Enter the maximum speed for the thrust graph.").setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane2", "Enter the name of a second plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane3", "Enter the name of a third plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane4", "Enter the name of a fourth plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane5", "Enter the name of a fifth plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane6", "Enter the name of a sixth plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane7", "Enter the name of a seventh plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "fuel_1", "Select the fuel percentage for plane 1.").setRequired(false).addChoice("0% fuel", 0).addChoice("30% fuel(min)", 30).addChoice("50% fuel", 50).addChoice("100% fuel(full)", 100),
                        new OptionData(OptionType.INTEGER, "fuel_2", "Select the fuel percentage for plane 2.").setRequired(false).addChoice("0% fuel", 0).addChoice("30% fuel(min)", 30).addChoice("50% fuel", 50).addChoice("100% fuel(full)", 100),
                        new OptionData(OptionType.INTEGER, "fuel_3", "Select the fuel percentage for plane 3.").setRequired(false).addChoice("0% fuel", 0).addChoice("30% fuel(min)", 30).addChoice("50% fuel", 50).addChoice("100% fuel(full)", 100),
                        new OptionData(OptionType.INTEGER, "fuel_4", "Select the fuel percentage for plane 4.").setRequired(false).addChoice("0% fuel", 0).addChoice("30% fuel(min)", 30).addChoice("50% fuel", 50).addChoice("100% fuel(full)", 100),
                        new OptionData(OptionType.INTEGER, "fuel_5", "Select the fuel percentage for plane 5.").setRequired(false).addChoice("0% fuel", 0).addChoice("30% fuel(min)", 30).addChoice("50% fuel", 50).addChoice("100% fuel(full)", 100),
                        new OptionData(OptionType.INTEGER, "fuel_6", "Select the fuel percentage for plane 6.").setRequired(false).addChoice("0% fuel", 0).addChoice("30% fuel(min)", 30).addChoice("50% fuel", 50).addChoice("100% fuel(full)", 100),
                        new OptionData(OptionType.INTEGER, "fuel_7", "Select the fuel percentage for plane 7.").setRequired(false).addChoice("0% fuel", 0).addChoice("30% fuel(min)", 30).addChoice("50% fuel", 50).addChoice("100% fuel(full)", 100)
                ).setDefaultPermissions(DefaultMemberPermissions.ENABLED)
        ).queue();

        event.getGuild().upsertCommand(Commands.slash("makedraggraph", "Create a thrust graph for a specific altitude.")
                .addOptions(
                        new OptionData(OptionType.INTEGER, "plane1", "Enter the name of the plane to create a thrust graph for.").setAutoComplete(true).setRequired(true),
                        new OptionData(OptionType.INTEGER, "alt", "Enter the altitude at which you want to create the graph.").setRequired(true),
                        new OptionData(OptionType.INTEGER, "minspeed", "Enter the minimum speed for the thrust graph.").setRequired(true),
                        new OptionData(OptionType.INTEGER, "maxspeed", "Enter the maximum speed for the thrust graph.").setRequired(true),
                        new OptionData(OptionType.INTEGER, "aoa", "Enter the target aoa.").setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane2", "Enter the name of a second plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane3", "Enter the name of a third plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane4", "Enter the name of a fourth plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane5", "Enter the name of a fifth plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane6", "Enter the name of a sixth plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane7", "Enter the name of a seventh plane to compare with the first plane.").setAutoComplete(true).setRequired(false)
                ).setDefaultPermissions(DefaultMemberPermissions.ENABLED)
        ).queue();
        event.getGuild().upsertCommand(Commands.slash("makedragthrustgraph", "Create an Acceleration graph for a specific altitude.")
                .addOptions(
                        new OptionData(OptionType.INTEGER, "plane1", "Enter the name of the plane to create a thrust graph for.").setAutoComplete(true).setRequired(true),
                        new OptionData(OptionType.INTEGER, "alt", "Enter the altitude at which you want to create the graph.").setRequired(true),
                        new OptionData(OptionType.INTEGER, "minspeed", "Enter the minimum speed for the thrust graph.").setRequired(true),
                        new OptionData(OptionType.INTEGER, "maxspeed", "Enter the maximum speed for the thrust graph.").setRequired(true),
                        new OptionData(OptionType.NUMBER, "aoa", "Enter the target aoa.").setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane2", "Enter the name of a second plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane3", "Enter the name of a third plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane4", "Enter the name of a fourth plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane5", "Enter the name of a fifth plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane6", "Enter the name of a sixth plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "plane7", "Enter the name of a seventh plane to compare with the first plane.").setAutoComplete(true).setRequired(false),
                        new OptionData(OptionType.INTEGER, "fuel_1", "Select the fuel percentage for plane 1.").setRequired(false).addChoice("0% fuel", 0).addChoice("30% fuel(min)", 30).addChoice("50% fuel", 50).addChoice("100% fuel(full)", 100),
                        new OptionData(OptionType.INTEGER, "fuel_2", "Select the fuel percentage for plane 2.").setRequired(false).addChoice("0% fuel", 0).addChoice("30% fuel(min)", 30).addChoice("50% fuel", 50).addChoice("100% fuel(full)", 100),
                        new OptionData(OptionType.INTEGER, "fuel_3", "Select the fuel percentage for plane 3.").setRequired(false).addChoice("0% fuel", 0).addChoice("30% fuel(min)", 30).addChoice("50% fuel", 50).addChoice("100% fuel(full)", 100),
                        new OptionData(OptionType.INTEGER, "fuel_4", "Select the fuel percentage for plane 4.").setRequired(false).addChoice("0% fuel", 0).addChoice("30% fuel(min)", 30).addChoice("50% fuel", 50).addChoice("100% fuel(full)", 100),
                        new OptionData(OptionType.INTEGER, "fuel_5", "Select the fuel percentage for plane 5.").setRequired(false).addChoice("0% fuel", 0).addChoice("30% fuel(min)", 30).addChoice("50% fuel", 50).addChoice("100% fuel(full)", 100),
                        new OptionData(OptionType.INTEGER, "fuel_6", "Select the fuel percentage for plane 6.").setRequired(false).addChoice("0% fuel", 0).addChoice("30% fuel(min)", 30).addChoice("50% fuel", 50).addChoice("100% fuel(full)", 100),
                        new OptionData(OptionType.INTEGER, "fuel_7", "Select the fuel percentage for plane 7.").setRequired(false).addChoice("0% fuel", 0).addChoice("30% fuel(min)", 30).addChoice("50% fuel", 50).addChoice("100% fuel(full)", 100)

                ).setDefaultPermissions(DefaultMemberPermissions.ENABLED)
        ).queue();


        event.getGuild().upsertCommand(Commands.slash("comparefms", "Compare the flight models of two planes.")
                .addOptions(
                        new OptionData(OptionType.INTEGER, "plane", "Enter the name of the first plane to compare.").setAutoComplete(true).setRequired(true),
                        new OptionData(OptionType.INTEGER, "plane2", "Enter the name of the second plane to compare.").setAutoComplete(true).setRequired(true),
                        new OptionData(OptionType.STRING, "gameversion1", "Enter the game version for the first plane.").setAutoComplete(true).setRequired(true),
                        new OptionData(OptionType.STRING, "gameversion2", "Enter the game version for the second plane.").setAutoComplete(true).setRequired(true)
                ).setDefaultPermissions(DefaultMemberPermissions.ENABLED)
        ).queue();

        event.getGuild().upsertCommand(Commands.slash("compareguns", "Compare two guns.")
                .addOptions(
                        new OptionData(OptionType.INTEGER, "gun", "Enter the name of the first gun to compare.").setAutoComplete(true).setRequired(true),
                        new OptionData(OptionType.INTEGER, "gun2", "Enter the name of the second gun to compare.").setAutoComplete(true).setRequired(true),
                        new OptionData(OptionType.STRING, "gameversion1", "Enter the game version for the first gun.").setAutoComplete(true).setRequired(true),
                        new OptionData(OptionType.STRING, "gameversion2", "Enter the game version for the second gun.").setAutoComplete(true).setRequired(true)
                ).setDefaultPermissions(DefaultMemberPermissions.ENABLED)
        ).queue();

        event.getGuild().upsertCommand(Commands.slash("comparemissiles", "Compare two missiles and see the difference in an embed.")
                .addOptions(
                        new OptionData(OptionType.INTEGER, "missile1", "Enter the name of the first missile to compare.").setAutoComplete(true).setRequired(true),
                        new OptionData(OptionType.INTEGER, "missile2", "Enter the name of the second missile to compare.").setAutoComplete(true).setRequired(true),
                        new OptionData(OptionType.STRING, "gameversion1", "Enter the game version for the first missile.").setAutoComplete(true).setRequired(true),
                        new OptionData(OptionType.STRING, "gameversion2", "Enter the game version for the second missile.").setAutoComplete(true).setRequired(true)
                ).setDefaultPermissions(DefaultMemberPermissions.ENABLED)
        ).queue();
    }

    public static final String help =
            "1. `/lookup`: Search for information about guns, planes, awards, and weapon presets.\n" +
                    "    - `plane`: Search for a specific plane.\n" +
                    "    - `award`: Search for a specific award.\n" +
                    "    - `weaponpresets`: View weapon presets for a specific plane.\n" +
                    "    - `gun`: Search for a specific gun.\n\n" +
                    "2. `/makethrustgraph`: Create a thrust graph for a specific altitude.\n" +
                    "    - `plane1` (**required**): Select the first plane to graph.\n" +
                    "    - `alt` (**required**): Specify the altitude for the graph.\n" +
                    "    - `minspeed` (optional): Enter the minimum speed for the thrust graph.\n" +
                    "    - `maxspeed` (optional): Enter the maximum speed for the thrust graph.\n" +
                    "    - `plane2`-`plane7` (optional): Select additional planes to compare.\n" +
                    "    - `fuel_1`-`fuel_7` (optional): Select the fuel percentage for each plane.\n\n" +
                    "3. `/makedraggraph`: Create a drag graph for a specific altitude.\n" +
                    "    - `plane1` (**required**): Select the first plane to graph.\n" +
                    "    - `alt` (**required**): Specify the altitude for the graph.\n" +
                    "    - `minspeed` (**required**): Enter the minimum speed for the thrust graph.\n" +
                    "    - `maxspeed` (**required**): Enter the maximum speed for the thrust graph.\n" +
                    "    - `aoa` (optional): Specify the aoa for the graph, if not set, the bot would dynamically change the AOA to maintain level flight.\n" +
                    "    - `plane2`-`plane7` (optional): Select additional planes to compare.\n\n" +
                    "4. `/makedragthrustgraph`: Create a thrust, drag and Acceleration graph for a specific altitude.\n" +
                    "    - `plane1` (**required**): Select the first plane to graph.\n" +
                    "    - `alt` (**required**): Specify the altitude for the graph.\n" +
                    "    - `minspeed` (**required**): Enter the minimum speed for the thrust graph.\n" +
                    "    - `maxspeed` (**required**): Enter the maximum speed for the thrust graph.\n" +
                    "    - `aoa` (optional): Specify the aoa for the graph, if not set, the bot would dynamically change the AOA to maintain level flight.\n" +
                    "    - `plane2`-`plane7` (optional): Select additional planes to compare.\n" +
                    "    - `fuel_1`-`fuel_7` (optional): Select the fuel percentage for each plane.\n\n" +
                    "5. `/comparefms`: Compare the flight models of two planes.\n" +
                    "    - `plane` and `plane2` (**required**): Select the two planes to compare.\n" +
                    "    - `gameversion1` and `gameversion2` (optional): Specify the game versions for each plane.\n\n" +
                    "6. `/compareguns`: Compare two guns.\n" +
                    "    - `gun` and `gun2` (**required**): Select the two guns to compare.\n" +
                    "    - `gameversion1` and `gameversion2` (optional): Specify the game versions for each gun.\n\n" +
                    "7. `/comparemissiles`: Compare two missiles and see the difference in an embed.\n" +
                    "   - `missile1` and `missile2`(**required**): Select the two missiles to compare.\n" +
                    "   - `gameversion1` and `gameversion2`(optional): Specify the game versions for each missile.";

}