package Info.templates;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Commit implements Serializable {
    public String sha;
    public String url;
    public String asujdakshd;
    @SerializedName("html_url")
    public String htmlUrl;
    @SerializedName("comments_url")
    public String commentsUrl;
    public GitCommit commit;

    @Override
    public String toString() {

        return "Commit{" +
                "sha='" + sha + '\'' +
                ", url='" + url + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", commentsUrl='" + commentsUrl + '\'' +
                ", commit=" + commit +
                '}';
    }
}
