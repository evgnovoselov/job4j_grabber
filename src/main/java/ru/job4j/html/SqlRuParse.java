package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;
import ru.job4j.model.Post;

import java.io.IOException;

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
                Post post = getPostFromUrl(href.attr("href"));
                post.setId(++postCounter);
                System.out.println(post);
                System.out.println("=======");
            }
        }
    }

    public static Post getPostFromUrl(String href) throws IOException {
        Document document = Jsoup.connect(href).get();
        String title = document.title().substring(0, document.title().indexOf(" / Вакансии"));
        Element description = document.select(".msgBody").get(1);
        Element created = document.select(".msgFooter").get(0);
        return new Post(
                title,
                href,
                description.text(),
                new SqlRuDateTimeParser().parse(created.text().substring(0, created.text().indexOf(" [")))
        );
    }
}
