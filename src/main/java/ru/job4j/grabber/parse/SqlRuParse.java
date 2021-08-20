package ru.job4j.grabber.parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.model.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {
    private final DateTimeParser dateTimeParser;

    private static int postCounterId;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        try {
            Document document = Jsoup.connect(link).get();
            Elements row = document.select(".postslisttopic");
            int skip = 3;
            for (Element element : row) {
                if (skip > 0) {
                    skip--;
                    continue;
                }
                Element href = element.child(0);
                posts.add(detail(href.attr("href")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post detail(String link) {
        Post post = null;
        try {
            Document document = Jsoup.connect(link).get();
            String title = document.title().substring(0, document.title().indexOf(" / Вакансии"));
            String time = document.select(".msgFooter").get(0).text();
            time = time.substring(0, time.indexOf(" ["));
            post = new Post(
                    title,
                    link,
                    document.select(".msgBody").get(1).text(),
                    dateTimeParser.parse(time)
            );
            post.setId(++postCounterId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return post;
    }
}
