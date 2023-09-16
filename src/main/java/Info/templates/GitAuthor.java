package Info.templates;

import java.io.Serializable;

public class GitAuthor implements Serializable {
    public String name;
    public String email;
    public String date;

    @Override
    public String toString() {
        return "GitAuthor{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
