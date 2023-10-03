package info.templates;

public class Award {
    public String name;
    public String link;
    public String description;
    public String imgLink;
    public long ID;

    public Award(String name, String description, String imgLink, long id) {
        this.ID = id;
        this.name = name;
        this.description = description;
        this.imgLink = imgLink;
    }
}
