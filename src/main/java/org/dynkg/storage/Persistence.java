package org.dynkg.storage;

import org.dynkg.datastructures.Graph;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Persistence {
    public static void saveGraph(Graph graph, String path) throws IOException {
        // Minimal JSON writer (nodes + edges)
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        // nodes
        sb.append("  \"nodes\": [");
        var entities = graph.listEntities();
        for(int i=0;i<entities.size();i++){
            sb.append("\"").append(escape(entities.get(i))).append("\"");
            if (i < entities.size()-1) sb.append(",");
        }
        sb.append("],\n");
        // edges (flatten)
        sb.append("  \"edges\": [\n");
        boolean firstEdge = true;
        for (String n : entities){
            for (var e : graph.edgesOf(n)){
                if (!firstEdge) sb.append(",\n");
                firstEdge = false;
                sb.append("    {");
                sb.append("\"src\":\"").append(escape(e.src)).append("\",");
                sb.append("\"rel\":\"").append(escape(e.rel)).append("\",");
                sb.append("\"dst\":\"").append(escape(e.dst)).append("\"}");
            }
        }
        sb.append("\n  ]\n");
        sb.append("}\n");
        Files.writeString(Path.of(path), sb.toString());
    }

    private static String escape(String s){ return s.replace("\\","\\\\").replace(""","\\""); }
}
