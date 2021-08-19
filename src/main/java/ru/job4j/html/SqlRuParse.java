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
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                Element parent = td.parent();
                if (parent != null) {
                    String date = parent.child(5).text();
                    System.out.println(date);
                    System.out.printf("LocalDateTime: %s%n", new SqlRuDateTimeParser().parse(date));
                }
                Post post = getPost(td);
                post.setId(++postCounter);
                System.out.println(post);
                System.out.println("=======");
            }
        }
    }

    private static Post getPost(Element element) throws IOException {
        Element hrefElement = element.child(0);
        String href = hrefElement.attr("href");
        String title = hrefElement.text();
        Document document = Jsoup.connect(href).get();
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
