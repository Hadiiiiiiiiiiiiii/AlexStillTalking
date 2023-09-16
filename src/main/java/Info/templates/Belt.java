package Info.templates;

import Info.generate.MakeGuns;

import java.io.Serializable;
import java.util.ArrayList;

public class Belt implements Serializable {
    String name;
    ArrayList<Shell> shells = new ArrayList<>();
    public Belt(String name)
    {
     //   System.out.println("LANG NAME: "+name);
        this.name = MakeGuns.getLangName(name);
    }
    public String getName() {
        return name;
    }

    public ArrayList<Shell> getShells() {
        return shells;
    }
    public void addShell(Shell shell)
    {
        shells.add(shell);
    }

    public String toString() {
        return
                "\n"+ name +":"+
                "\n     shells: " + getShellNames()+"\n";
    }
    private String getShellNames() {
        var str = new StringBuilder();
        for (int i = 0; i < shells.size(); i++) {
            str.append(shells.get(i).type);
            if (i != shells.size() - 1) {
                str.append(", ");
            }
        }
        return str.toString();
    }

}
