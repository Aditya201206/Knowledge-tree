package org.dynkg.ingestion;

import java.util.regex.*;
import java.util.*;

public class Preprocess {
    public static String cleanText(String text){
        text = text.replaceAll("<[^>]*>", " ");
        text = text.replaceAll("[^A-Za-z0-9.,;:?! ()'"_-]", " ");
        text = text.replaceAll("\s+", " ").trim();
        return text;
    }
    public static List<String> sentences(String text){
        return Arrays.asList(text.split("(?<=[.?!])\s+"));
    }
}
