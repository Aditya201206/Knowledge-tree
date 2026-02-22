package org.dynkg;

import java.nio.file.*;
import java.util.*;

public class AppMain {
    public static void main(String[] args) throws Exception {
        String dict = "data/dictionary.txt";
        String input = "data/sample.txt";
        String save = "data/graph.json";
        for (int i=0;i<args.length-1;i++){
            if (args[i].equals("--dictionary")) dict = args[i+1];
            if (args[i].equals("--input")) input = args[i+1];
            if (args[i].equals("--save")) save = args[i+1];
        }

        App app = new App();
        if (Files.exists(Path.of(dict))) app.loadDictionary(dict);
        if (Files.exists(Path.of(input))) app.ingestFile(input);
        var phrases = app.mineAndExtend();
        System.out.println("Mined phrases: " + phrases);
        var newTriples = app.extract();
        System.out.println("New triples: " + newTriples);
        long ts = app.commit();
        System.out.println("Committed at timestamp: " + ts);

        System.out.println("Entities: " + app.query("LIST ENTITIES"));
        if (!newTriples.isEmpty()){
            var h = newTriples.get(0).h;
            var t = newTriples.get(0).t;
            System.out.println("Rels of " + h + ": " + app.query("RELS OF " + h));
            System.out.println("Path " + h + " to " + t + ": " + app.query("PATH " + h + " TO " + t));
            System.out.println("Temporal rels of " + h + ": " + app.query("AT " + ts + " RELS OF " + h));
        }

        app.save(save);
        System.out.println("Saved graph to " + save);
    }
}
