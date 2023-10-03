package info.templates;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class WikiManager {
    public ArrayList<Award> awards = new ArrayList<>();
    public WikiManager()
    {
       loadAwards();
    }

    public void loadAwards()
    {
        try {
            Document doc = null;
            try {
                doc = Jsoup.connect("https://wiki.warthunder.com/Awards").get();
            } catch (IOException e) {
                System.out.println("FUCK you");
                e.printStackTrace();
            }
            Elements info = doc.getElementsByClass("mw-parser-output").get(0).select("tr");
            for (int i = 0; i < info.size(); i++) {
                String name = info.get(i).select("td").get(1).select("b").text();
                String imgLink = info.get(i).select("td").get(0).select("img[src~=(?i)\\.(png|jpe?g|gif)]").attr("src");
                String disc = info.get(i).select("td").get(1).text().replace(name, "");
                //  System.out.println(imgLink);
                Award a = new Award(name, disc, imgLink, new Random().nextLong());
                awards.add(a);
                // System.out.println(awards.size());
            }
        }
        catch (Exception ee) {
            System.out.println("awards did NOT load");
        }
    }
    public Award getAward(long id)
    {
        for (int i = 0; i < awards.size(); i++) {
            if (awards.get(i).ID == id)
                return awards.get(i);
        }
        return null;
    }
}
