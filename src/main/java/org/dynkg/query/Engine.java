package org.dynkg.query;

import org.dynkg.datastructures.Graph;
import org.dynkg.storage.VersionManager;

public class Engine {
    public static Object execute(Graph graph, VersionManager vm, String q){
        var ast = Parser.parse(q);
        return switch(ast.type()){
            case "LIST_ENTITIES" -> graph.listEntities();
            case "RELS_OF" -> graph.edgesOf(ast.node());
            case "PATH" -> graph.bfsPath(ast.a(), ast.b(), 6);
            case "TEMPORAL_RELS_OF" -> {
                var g = vm.rebuildGraphAt(ast.timestamp());
                yield g.edgesOf(ast.node());
            }
            default -> "Unsupported query: " + q;
        };
    }
}
