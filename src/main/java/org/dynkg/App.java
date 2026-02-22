package org.dynkg;

import org.dynkg.datastructures.Graph;
import org.dynkg.extraction.Deduplicator;
import org.dynkg.extraction.ERE;
import org.dynkg.ingestion.DictionaryNER;
import org.dynkg.ingestion.Preprocess;
import org.dynkg.ingestion.PhraseMining;
import org.dynkg.query.Engine;
import org.dynkg.storage.Persistence;
import org.dynkg.storage.VersionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class App {
    public final Graph graph = new Graph();
    public final DictionaryNER ner = new DictionaryNER();
    public final VersionManager versionMgr = new VersionManager("data/versions");
    public final Deduplicator dedup = new Deduplicator();
    public String textCorpus = "";
    public final List<ERE.Triple> staging = new ArrayList<>();

    public void loadDictionary(String path) throws IOException {
        ner.loadFromFile(path);
    }
    public int ingestFile(String path) throws IOException {
        String raw = Files.readString(Path.of(path));
        textCorpus = Preprocess.cleanText(raw);
        return textCorpus.length();
    }
    public List<String> mineAndExtend(){
        List<String> phrases = PhraseMining.minePhrases(textCorpus, 6, 2, 20);
        for (String p : phrases) ner.insertPhrase(p);
        return phrases;
    }
    public List<ERE.Triple> extract(){
        List<ERE.Triple> triples = ERE.extractTriples(textCorpus);
        List<ERE.Triple> fresh = dedup.filterNew(triples);
        staging.addAll(fresh);
        return fresh;
    }
    public long commit(){
        var rec = versionMgr.commit(graph, staging);
        return rec.ts;
    }
    public Object query(String q){
        return Engine.execute(graph, versionMgr, q);
    }
    public void save(String path) throws IOException {
        Persistence.saveGraph(graph, path);
    }
}
