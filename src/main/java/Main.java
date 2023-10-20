import info.AlexStillTalking;
import info.VersionChecker;
import info.generate.Comparator;
import info.templates.Gun;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.apache.commons.lang3.SerializationUtils;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static String gunSaveLoc = "Data/Ser/guns.ser";

    public static void main(String[] args) throws LoginException, IOException {
        deleteTmp();
        System.setProperty("java.awt.headless", "true");
        System.out.println("MAIN VERSION 0.0.1");
        ArrayList<Gun> l = readGuns();

        AlexStillTalking l2 = new AlexStillTalking(l);
        var token = Files.readString(Path.of("tokens.env")).split(" ")[0];

        JDA api = JDABuilder.createDefault(token)
                .setActivity(Activity.watching("You"))
                .addEventListeners(l2)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .build();
        l2.setApi(api);
        try {
            api.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(api.getInviteUrl(Permission.MESSAGE_HISTORY, Permission.VIEW_CHANNEL, Permission.USE_APPLICATION_COMMANDS));
        List<Guild> gulids = api.getGuilds();
        int j = 0;
        for (int i = 0; i < gulids.size(); i++) {
            j++;
            gulids.get(i).upsertCommand(Commands.slash("help", "Get help on how to use this bot.")
                    .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
            ).queue();

            gulids.get(i).upsertCommand(Commands.slash("lookup", "Search for information about guns, planes, awards, and weapon presets.")
                    .addOptions(new OptionData(OptionType.INTEGER, "plane", "Enter the name of the plane you want to search for.").setAutoComplete(true).setRequired(false),
                            new OptionData(OptionType.STRING, "award", "Enter the name of the award you want to search for.").setAutoComplete(true).setRequired(false),
                            new OptionData(OptionType.INTEGER, "weaponpresets", "Enter the name of the plane to view its weapon presets.").setAutoComplete(true).setRequired(false),
                            new OptionData(OptionType.INTEGER, "gun", "Enter the name of the gun you want to search for.").setAutoComplete(true).setRequired(false)
                    ).setDefaultPermissions(DefaultMemberPermissions.ENABLED)
            ).queue();

            gulids.get(i).upsertCommand(Commands.slash("makethrustgraph", "Create a thrust graph for a specific altitude.")
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


            gulids.get(i).upsertCommand(Commands.slash("getdrag", "Get the drag of a plane at a specific speed (does not include wave drag).")
                    .addOptions(
                            new OptionData(OptionType.INTEGER, "plane", "Enter the name of the plane to get the drag for.").setAutoComplete(true).setRequired(true),
                            new OptionData(OptionType.INTEGER, "alt", "Enter the altitude at which you want to get the drag.").setRequired(true),
                            new OptionData(OptionType.INTEGER, "speed", "Enter the speed at which you want to get the drag.").setAutoComplete(false).setRequired(true)
                    ).setDefaultPermissions(DefaultMemberPermissions.ENABLED)
            ).queue();

            gulids.get(i).upsertCommand(Commands.slash("comparefms", "Compare the flight models of two planes.")
                    .addOptions(
                            new OptionData(OptionType.INTEGER, "plane", "Enter the name of the first plane to compare.").setAutoComplete(true).setRequired(true),
                            new OptionData(OptionType.INTEGER, "plane2", "Enter the name of the second plane to compare.").setAutoComplete(true).setRequired(true),
                            new OptionData(OptionType.STRING, "gameversion1", "Enter the game version for the first plane.").setAutoComplete(true).setRequired(true),
                            new OptionData(OptionType.STRING, "gameversion2", "Enter the game version for the second plane.").setAutoComplete(true).setRequired(true)
                    ).setDefaultPermissions(DefaultMemberPermissions.ENABLED)
            ).queue();

            gulids.get(i).upsertCommand(Commands.slash("compareguns", "Compare two guns.")
                    .addOptions(
                            new OptionData(OptionType.INTEGER, "gun", "Enter the name of the first gun to compare.").setAutoComplete(true).setRequired(true),
                            new OptionData(OptionType.INTEGER, "gun2", "Enter the name of the second gun to compare.").setAutoComplete(true).setRequired(true),
                            new OptionData(OptionType.STRING, "gameversion1", "Enter the game version for the first gun.").setAutoComplete(true).setRequired(true),
                            new OptionData(OptionType.STRING, "gameversion2", "Enter the game version for the second gun.").setAutoComplete(true).setRequired(true)
                    ).setDefaultPermissions(DefaultMemberPermissions.ENABLED)
            ).queue();

            gulids.get(i).upsertCommand(Commands.slash("comparemissiles", "Compare two missiles and see the difference in an embed.")
                    .addOptions(
                            new OptionData(OptionType.INTEGER, "missile1", "Enter the name of the first missile to compare.").setAutoComplete(true).setRequired(true),
                            new OptionData(OptionType.INTEGER, "missile2", "Enter the name of the second missile to compare.").setAutoComplete(true).setRequired(true),
                            new OptionData(OptionType.STRING, "gameversion1", "Enter the game version for the first missile.").setAutoComplete(true).setRequired(true),
                            new OptionData(OptionType.STRING, "gameversion2", "Enter the game version for the second missile.").setAutoComplete(true).setRequired(true)
                    ).setDefaultPermissions(DefaultMemberPermissions.ENABLED)
            ).queue();


        }
        Comparator.readCommits();

        try {
            api.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
        try {
            //  api.awaitReady();
            api.awaitReady();
        } catch (InterruptedException e) {
            System.out.println("ERR\n" + e);
        }
        var versionChecker = new VersionChecker(api);
        versionChecker.start();

    }

    private static ArrayList<Gun> readGuns() {
        ArrayList<Gun> l;

        try {
            var f = new File(gunSaveLoc);
            BufferedInputStream bis = new BufferedInputStream(f.toURL().openStream());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            for (int result = bis.read(); result != -1; result = bis.read()) {
                buf.write((byte) result);
            }
            byte[] data = buf.toByteArray();
            buf.close();
            l = SerializationUtils.deserialize(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return l;
    }


    public static void deleteTmp() {
        File directory = new File("Data/OtherGraphs");
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length - 1; i++) {
            files[i].delete();
        }
        directory = new File("Data/Scripts/tmp");
        files = directory.listFiles();
        for (int i = 0; i < files.length - 1; i++) {
            files[i].delete();
        }
        directory = new File("Data/Scripts/txts");
        files = directory.listFiles();
        for (int i = 0; i < files.length - 1; i++) {
            files[i].delete();
        }
    }

}
