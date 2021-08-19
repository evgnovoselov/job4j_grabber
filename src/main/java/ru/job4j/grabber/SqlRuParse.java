package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.model.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {
    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        Document document;
        try {
            document = Jsoup.connect(link).get();
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
        return null;
    }
}
