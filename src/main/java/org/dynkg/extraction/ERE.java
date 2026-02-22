package org.dynkg.extraction;

import java.util.*;
import java.util.regex.*;
import org.dynkg.ingestion.Preprocess;

public class ERE {
    public static class Triple {
        public final String h, r, t;
        public final Map<String, String> props;

        public Triple(String h, String r, String t, Map<String, String> p) {
            this.h = h;
            this.r = r;
            this.t = t;
            this.props = p;
        }

        public String key() {
            return h + "::" + r + "::" + t;
        }

        public String toString() {
            return "(" + h + ", " + r + ", " + t + ")";
        }
    }

    private static final List<Map.Entry<Pattern, String>> RELS = List.of(
            Map.entry(
                    Pattern.compile("(?i)([A-Z][A-Za-z0-9_'-]+)\\s+(works\\s+for|joined|at)\\s+([A-Z][A-Za-z0-9_'-]+)"),
                    "WORKS_FOR"),
            Map.entry(Pattern.compile("(?i)([A-Z][A-Za-z0-9_'-]+)\\s+(founded|started)\\s+([A-Z][A-Za-z0-9_'-]+)"),
                    "FOUNDED"),
            Map.entry(Pattern.compile("(?i)([A-Z][A-Za-z0-9_'-]+)\\s+(acquired|bought)\\s+([A-Z][A-Za-z0-9_'-]+)"),
                    "ACQUIRED"));

    public static List<Triple> extractTriples(String text) {
        List<Triple> triples = new ArrayList<>();
        for (String sent : Preprocess.sentences(text)) {
            for (var e : RELS) {
                Matcher m = e.getKey().matcher(sent);
                while (m.find()) {
                    String h = m.group(1).trim();
                    String t = m.group(3).trim();
                    Map<String, String> props = new HashMap<>();
                    props.put("source_sentence", sent.trim());
                    triples.add(new Triple(h, e.getValue(), t, props));
                }
            }
        }
        return triples;
    }
}
