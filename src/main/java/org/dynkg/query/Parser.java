package org.dynkg.query;

import java.util.regex.*;

public class Parser {
    public static record AST(String type, String a, String b, String label, Long timestamp, String node) {}

    public static AST parse(String q){
        q = q.trim();
        if (q.matches("(?i)^LIST\s+ENTITIES$")) return new AST("LIST_ENTITIES", null,null,null,null,null);
        var m = Pattern.compile("(?i)^LIST\s+ENTITIES\s+WITH\s+LABEL\s+(\w+)$").matcher(q);
        if (m.find()) return new AST("LIST_ENTITIES", null,null,m.group(1),null,null);
        m = Pattern.compile("(?i)^RELS\s+OF\s+(.+)$").matcher(q);
        if (m.find()) return new AST("RELS_OF", null,null,null,null,m.group(1).trim());
        m = Pattern.compile("(?i)^PATH\s+(.+?)\s+TO\s+(.+)$").matcher(q);
        if (m.find()) return new AST("PATH", m.group(1).trim(), m.group(2).trim(), null,null,null);
        m = Pattern.compile("(?i)^AT\s+(\d{13})\s+RELS\s+OF\s+(.+)$").matcher(q);
        if (m.find()) return new AST("TEMPORAL_RELS_OF", null,null,null, Long.parseLong(m.group(1)), m.group(2).trim());
        return new AST("UNKNOWN", null,null,null,null,q);
    }
}
