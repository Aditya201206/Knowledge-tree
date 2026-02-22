package org.dynkg.ingestion;

import org.dynkg.datastructures.Trie;
import java.nio.file.*;
import java.util.*;
import java.io.*;

public class DictionaryNER {
    private final Trie trie = new Trie();

    public void loadFromFile(String path) throws IOException {
        for (String line : Files.readAllLines(Path.of(path))){
            line = line.trim();
            if (!line.isEmpty()) trie.insert(line);
        }
    }

    public List<String[]> tagText(String text){
        List<String[]> matches = new ArrayList<>();
        int i = 0;
        while (i < text.length()){
            int l = trie.longestMatchFrom(text, i);
            if (l > 0){
                String phrase = text.substring(i, i+l);
                matches.add(new String[]{ String.valueOf(i), String.valueOf(i+l), phrase});
                i += l;
            }else i++;
        }
        return matches;
    }

    public List<String> suggest(String prefix, int limit){
        return trie.suggest(prefix, limit);
    }
}


    // Convenience to add a single phrase (used by phrase mining feedback)
    public void insertPhrase(String phrase){
        trie.insert(phrase);
    }

    // Back-compat for App.mineAndExtend()
    public void loadFromFileString(String phrase){
        insertPhrase(phrase);
    }
