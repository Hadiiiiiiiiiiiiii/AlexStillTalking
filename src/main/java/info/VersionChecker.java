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
import java.util.concurrent.atomic.AtomicReference;

public class VersionChecker {
    private static final String URL_DEV = "https://yupmaster.gaijinent.com/yuitem/get_version.php?proj=warthunder&tag=dev";
    private static final String URL_LIVE = "https://yupmaster.gaijinent.com/yuitem/get_version.php?proj=warthunder";
    private static final String SNAIL_NEWS = "768492504638816257";
    private static final String WAR_THUNDER = "712702905529925713";
    private static final Path VERSION_FILE_DEV = Paths.get("Data/Other/gameVersion_dev");
    private static final Path VERSION_FILE_LIVE = Paths.get("Data/Other/gameVersion_live");
    private final JDA jda;
    private final OkHttpClient httpClient;
    private final AtomicReference<String> currentVersionDev = new AtomicReference<>();
    private final AtomicReference<String> currentVersionLive = new AtomicReference<>();

    public VersionChecker(JDA jda) {
        this.jda = jda;
        this.httpClient = new OkHttpClient();
    }

    public void start() {
        try {
            if (Files.exists(VERSION_FILE_DEV)) {
                currentVersionDev.set(Files.readString(VERSION_FILE_DEV).trim());
            }
            if (Files.exists(VERSION_FILE_LIVE)) {
                currentVersionLive.set(Files.readString(VERSION_FILE_LIVE).trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> checkVersion(URL_DEV, VERSION_FILE_DEV, "dev", currentVersionDev), 0, 10, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(() -> checkVersion(URL_LIVE, VERSION_FILE_LIVE, "live", currentVersionLive), 0, 10, TimeUnit.SECONDS);
    }


    private void checkVersion(String url, Path versionFile, String versionType, AtomicReference<String> currentVersionRef) {
        try {
            Request request = new Request.Builder().url(url).build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String version = response.body().string().trim();
                String currentVersion = currentVersionRef.get();
                if (currentVersion == null) {
                    currentVersionRef.set(version);
                } else if (!currentVersion.equals(version)) {
                    String[] oldParts = currentVersion.split("\\.");
                    String[] newParts = version.split("\\.");

                    TextChannel channel1 = jda.getTextChannelById(SNAIL_NEWS);
                 //   if (channel1 != null) {
                 //       channel1.sendMessage("Game " + versionType + " version changed: " + version).queue();
                 //   }

                    if (!oldParts[0].equals(newParts[0]) || !oldParts[1].equals(newParts[1])) {
                        TextChannel channel2 = jda.getTextChannelById(WAR_THUNDER);
                        if (channel2 != null) {
                            channel2.sendMessage("Major Game " + versionType + " version changed: " + version).queue();
                        }
                    }
                    currentVersionRef.set(version);

                    currentVersion = version;

                    try {
                        Files.writeString(versionFile, currentVersion);
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