package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class LinkCrawler extends RecursiveAction {

    private final String url;

    public LinkCrawler(String url) {
        this.url = url;
    }

    @Override
    protected void compute() {
        Set<String> siteUrls = extractUrls(url);
        if (siteUrls.isEmpty()) { // если на странице нет подходящих ссылок, прерывем наш метод
            return;
        }
        List<LinkCrawler> taskList = new ArrayList<>(); // создаём список для хранения задач

        for (String siteUrl : siteUrls) {
            LinkCrawler task = new LinkCrawler(siteUrl); // создаем новую задачу уже с другой ссылкой
            task.fork(); // запускаем задачу асинхронно
            taskList.add(task); // добавляем задачу в список
        }
        taskList.forEach(ForkJoinTask::join);  // ожидаем завершения всех задач
    }

    private Set<String> extractUrls(String url) {
        Set<String> urlList = new HashSet<>();
        Document doc = getDocumentFromUrl(url);
        if (doc != null) {
            Elements elements = doc.select("a"); // достаем все элементы CSS со значением "a"

            for (Element el : elements) { // проходим по всем элементам
                String absUrl = el.attr("abs:href"); // достаем абсолютную ссылку
                if (isValidUrl(absUrl)) { // проверяем ссылку
                    urlList.add(absUrl); // добавляем в список
                    UrlRepository.URLS.add(absUrl); // добавляем в общий список ссылок
                }
            }
        }
        return urlList;
    }

    private boolean isValidUrl(String url) {
        return !url.isEmpty() // проверяем, что ссылка не пустая
                && !url.contains("#") // отсеиваем внутренние элементы
                && !UrlRepository.URLS.contains(url) // проверяем, нет ли такой ссылки в нашем хранилище
                && url.startsWith(Main.PARENT_URL); // проверяем, что ссылка соответсовует родительской
    }

    private Document getDocumentFromUrl(String url) {
        try {
            System.out.println("Parsing URL with address: " + url);
            Thread.sleep(150); // добавляем задержку перед каждый запросом
            return Jsoup.connect(url).get(); // делаем GET запрос по URL
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
