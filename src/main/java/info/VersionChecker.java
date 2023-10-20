package info;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VersionChecker {
    private static final String URL = "https://yupmaster.gaijinent.com/yuitem/get_version.php?proj=warthunder&tag=dev";
    private static final String SNAIL_NEWS = "768492504638816257";
    private static final String WAR_THUNDER = "712702905529925713";
    private static final Path VERSION_FILE = Paths.get("Data/Other/gameVersion");

    private final JDA jda;
    private final OkHttpClient httpClient;
    private String currentVersion;

    public VersionChecker(JDA jda) {
        this.jda = jda;
        this.httpClient = new OkHttpClient();
    }

    public void start() {
        try {
            if (Files.exists(VERSION_FILE)) {
                currentVersion = Files.readString(VERSION_FILE).trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::checkVersion, 0, 10, TimeUnit.SECONDS);
    }

    private void checkVersion() {
        try {
            Request request = new Request.Builder().url(URL).build();
            Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String version = response.body().string().trim();
                if (currentVersion == null) {
                    currentVersion = version;
                } else if (!currentVersion.equals(version)) {
                    String[] oldParts = currentVersion.split("\\.");
                    String[] newParts = version.split("\\.");

                    TextChannel channel1 = jda.getTextChannelById(SNAIL_NEWS);
                    if (channel1 != null) {
                        channel1.sendMessage("Game version changed: " + version).queue();
                    }

                    if (!oldParts[0].equals(newParts[0]) || !oldParts[1].equals(newParts[1])) {
                        TextChannel channel2 = jda.getTextChannelById(WAR_THUNDER);
                        if (channel2 != null) {
                            channel2.sendMessage("Major Game version changed: " + version).queue();
                        }
                    }

                    currentVersion = version;

                    try {
                        Files.writeString(VERSION_FILE, currentVersion);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
