package Info;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class Interaction implements Serializable {
    public long msgId;
    private long txtcnl;

    private long authorid;

    private int currentpage = 0;
    public ArrayList<String> splitFile = new ArrayList<>();
    private String plane1;
    private String plane2;
    private String gamever1;
    private String gamever2;
    public Message msg = null;
    ArrayList<Page> pages = new ArrayList<>();

    private int currentPage = 0;

    public Interaction(long txtcnl, long authorid, String filecontent, String plane1, String plane2, String gamever1, String gamever2)
    {//
        //this.embed = embed;
        this.txtcnl = txtcnl;
        this.authorid = authorid;
        this.plane1 = plane1;
        this.plane2 = plane2;
        this.gamever1 = gamever1;
        this.gamever2 = gamever2;
        String[] s;
        try {
            s = Files.readString(Path.of(filecontent)).split("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        splitFile.addAll(Arrays.asList(s));
        SetUpPages(pages);
    }

    public long getAuthorid() {
        return authorid;
    }


    public EmbedBuilder getNextPage() {
        if (pages.size() > currentPage+1)
            if (pages.get(currentPage).nextPage != null) {
                currentPage++;
                EmbedBuilder embed = new EmbedBuilder()
                        .setColor(Color.WHITE)
                        .setFooter("page "+(currentPage+1)+" / "+(pages.size()), "https://cdn.discordapp.com/attachments/900107026993152011/977188607557242941/unknown.png").setTitle("differences between " + plane1 + " and " + plane2 + " on versions " + gamever1 + " and " + gamever2 + " are: ");
                embed.setDescription("```diff\n" + pages.get(currentPage).content + "```");

                return embed;
            }
        return null;
    }

    public EmbedBuilder getPrevPage() {
        if (pages.get(currentPage).prevPage != null) {
            currentPage--;

            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Color.WHITE)
                    .setFooter("page "+(currentPage+1)+" / "+(pages.size()), "https://cdn.discordapp.com/attachments/900107026993152011/977188607557242941/unknown.png").setTitle("differences between " + plane1 + " and " + plane2 + " on versions " + gamever1 + " and " + gamever2 + " are: ");
            embed.setDescription("```diff\n" + pages.get(currentPage).content + "```");


            return embed;
        }
        return null;
    }
    public EmbedBuilder showFirstPage()
    {
        currentPage = 0;
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.white)
                .setFooter("page "+(currentPage+1)+" / "+(pages.size()), "https://cdn.discordapp.com/attachments/900107026993152011/977188607557242941/unknown.png").setTitle("differences between " + plane1 + " and " + plane2 + " on versions " + gamever1 + " and " + gamever2 + " are: ");
        embed.setTitle("difference between "+plane1+" and " +plane2+" on versions "+gamever1 + " and "+gamever2+" are: ");
        embed.setDescription("```diff\n"+pages.get(currentPage).content+"```");
        return embed;
    }
    public EmbedBuilder showLastPage()
    {
        currentPage = pages.size()-1;

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.white)
                .setFooter("page "+(currentPage+1)+" / "+(pages.size()), "https://cdn.discordapp.com/attachments/900107026993152011/977188607557242941/unknown.png").setTitle("differences between " + plane1 + " and " + plane2 + " on versions " + gamever1 + " and " + gamever2 + " are: ");
        embed.setTitle("difference between "+plane1+" and " +plane2+" on versions "+gamever1 + " and "+gamever2+" are: ");
        embed.setDescription("```diff\n"+pages.get(currentPage).content+"```");
        //currentPage = pages.size()-1;

        return embed;
    }
    private void SetUpPages(ArrayList<Page> pages)
    {
        StringBuilder b = new StringBuilder();
        int t = 0;
        int j = 0;
        Page prevPage = null;
        Page nextPage = null;
        int num = 0;
        while (t < splitFile.size())
        {
            b.append(splitFile.get(t)).append("\n");
            if (t == splitFile.size()-1 || j >= 25)
            {
                Page p = new Page(nextPage, prevPage, b.toString(), num);
                pages.add(p);
                if (prevPage != null)
                {
                    prevPage.nextPage = p;
                }
                nextPage = p.CopyPage();
                b.setLength(0);
                j = 0;
                prevPage = p;
                num++;
            }
            j++;
            t++;
        }
    }
    private Page getPageById(int num)
    {
        for (int i = 0; i < pages.size(); i++) {
            if (pages.get(i).pageNum == num)
            {
                return pages.get(i);
            }
        }
        return null;
    }


    class Page
    {
        Page nextPage;
        Page prevPage;
        String content;
        int pageNum;

        Page(Page nextPage, Page prevPage, String content, int pageNum) {
            this.nextPage = nextPage;
            this.prevPage = prevPage;
            this.content = content;
            this.pageNum = pageNum;
        }
        Page CopyPage()
        {
            return new Page(nextPage ,prevPage ,content ,pageNum);
        }

    }
    public String getPlane1() {
        return plane1;
    }

    public String getPlane2() {
        return plane2;
    }

    public String getGamever1() {
        return gamever1;
    }

    public String getGamever2() {
        return gamever2;
    }
}