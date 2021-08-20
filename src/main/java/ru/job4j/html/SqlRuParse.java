package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;
import ru.job4j.grabber.model.Post;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SqlRuParse {

    private static int postCounter = 0;

    public static void main(String[] args) throws IOException {
        for (int page = 1; page < 6; page++) {
            Document doc = Jsoup.connect(String.format("https://www.sql.ru/forum/job-offers/%s", page)).get();
            Elements row = doc.select(".postslisttopic");
            int skip = 3;
            for (Element td : row) {
                if (skip > 0) {
                    skip--;
                    continue;
                }
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                Element parent = td.parent();
                if (parent != null) {
                    String date = parent.child(5).text();
                    System.out.println(date);
                    System.out.printf("LocalDateTime: %s%n", new SqlRuDateTimeParser().parse(date));
                }
                Map<String, String> detail = getDetailPostData(href.attr("href"));
                Post post = new Post(
                        href.text(),
                        href.attr("href"),
                        detail.get("description"),
                        new SqlRuDateTimeParser().parse(detail.get("created"))
                );
                post.setId(++postCounter);
                System.out.println(post);
                System.out.println("=======");
            }
        }
    }

    public static Map<String, String> getDetailPostData(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Map<String, String> detail = new HashMap<>();
        detail.put("description", document.select(".msgBody").get(1).text());
        String time = document.select(".msgFooter").get(0).text();
        time = time.substring(0, time.indexOf(" ["));
        detail.put("created", time);
        return detail;
    }
}
