package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static final String PARENT_URL = "https://metanit.com/java";

    public static void main(String[] args) {
        try (ForkJoinPool forkJoinPool = new ForkJoinPool()) { // используем 'try'-with-resources
            forkJoinPool.invoke(new LinkCrawler(PARENT_URL));
        }
        writeToFile(); // запись в файл
    }

    private static void writeToFile() {
        File file = createFile();
        try (FileWriter fileWriter = new FileWriter(file)) {
            for (String url : UrlRepository.URLS) { // проходим по всем нашим ссылкам
                fileWriter.write(getFormattedUrl(url)); // записываем ссылки в отформатированном формате
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File createFile() {
        String filePath = "map.txt";
        Path path = Path.of(filePath);
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
            return new File(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFormattedUrl(String url) {
        String[] elements = url.split("/"); // делим ссылку по "/"
        int tabCount = Math.max(0, elements.length - 3); // считаем сколько отступов надо сделать
        return "\t".repeat(tabCount) + url + "\n"; // возвращаем ссылку в нужном формате
    }
}