package org.example;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class UrlRepository {
    public final static Set<String> URLS = new ConcurrentSkipListSet<>();
}
