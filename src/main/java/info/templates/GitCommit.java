package info.templates;

import java.io.Serializable;

public class GitCommit implements Serializable {
    public String message;
    public GitAuthor author;
    public GitAuthor committer;

    @Override
    public String toString() {
        return "GitCommit{" +
                "message='" + message + '\'' +
                ", author=" + author +
                ", committer=" + committer +
                '}';
    }
}
