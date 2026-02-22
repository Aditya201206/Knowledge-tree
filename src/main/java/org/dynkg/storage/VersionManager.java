package org.dynkg.storage;

import org.dynkg.datastructures.BTree;
import org.dynkg.datastructures.Graph;
import org.dynkg.extraction.ERE;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class VersionManager {
    private final Path baseDir;
    private final BTree<Long, String> index = new BTree<>(2);
    private int counter = 0;
    private final List<Record> records = new ArrayList<>();

    public static class Record {
        public long ts;
        public String vid;
        public List<ERE.Triple> triples;
    }

    public VersionManager(String dir) {
        this.baseDir = Path.of(dir);
        try {
            Files.createDirectories(baseDir);
        } catch (IOException ignored) {
        }
    }

    public Record commit(Graph graph, List<ERE.Triple> staged) {
        long ts = System.currentTimeMillis();
        String vid = "v"+(++counter);
        // apply
        for (var tr : staged){
            graph.addNode(tr.h);
            graph.addNode(tr.t);
            graph.addEdge(tr.h, tr.r, tr.t, new HashMap<>(tr.props));
        }
        // remember in memory for temporal rebuilds
        Record rec = new Record();
        rec.ts = ts; rec.vid = vid; rec.triples = new ArrayList<>(staged);
        records.add(rec);
        index.insert(ts, vid);
        // persist minimal JSON lines
        Path p = baseDir.resolve(vid + ".jsonl");
        try (BufferedWriter w = Files.newBufferedWriter(p)){
            for (var tr : staged){
                w.write(String.format("{"h":"%s","r":"%s","t":"%s"}\n",
                    escape(tr.h), escape(tr.r), escape(tr.t)));
            }
        }catch(IOException e){ throw new RuntimeException(e); }
        staged.clear();
        return rec;
    }

    private String escape(String s){ return s.replace("\\","\\\\").replace(""","\\""); }

    public List<Map.Entry<Long, String>> listVersions() {
        return index.inorder();
    }

    public Graph rebuildGraphAt(long timestamp) {
        Graph g = new Graph();
        for (Record r : records) {
            if (r.ts > timestamp)
                break;
            for (var tr : r.triples) {
                g.addNode(tr.h);
                g.addNode(tr.t);
                g.addEdge(tr.h, tr.r, tr.t, new HashMap<>(tr.props));
            }
        }
        return g;
    }
}
