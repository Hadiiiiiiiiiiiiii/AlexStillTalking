package info.templates;

import java.io.Serializable;

public class Missile implements Serializable {
    public String actualName;
    String actualname;
    String name;
    int id;
//this class is used for comparing different missiles on different game versions.
    public Missile(String actualname, String name, int id) {
        this.actualname = actualname;
        this.name = name;
        this.id = id;
    }

    public String getActualname() {
        return actualname;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Missile{" +
                "actualname='" + actualname + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
