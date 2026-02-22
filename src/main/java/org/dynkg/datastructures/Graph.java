package org.dynkg.datastructures;

import java.util.*;

public class Graph {
    public static class Node {
        public final Set<String> labels = new HashSet<>();
        public final Map<String, Object> props = new HashMap<>();
    }
    private final Map<String, Node> nodes = new HashMap<>();
    private final Map<String, Map<String, List<Edge>>> adj = new HashMap<>();

    public static class Edge {
        public final String src, rel, dst;
        public final Map<String, Object> props;
        public Edge(String s, String r, String d, Map<String,Object> p){ src=s; rel=r; dst=d; props=p; }
    }

    public void addNode(String id, Collection<String> labels, Map<String,Object> props){
        Node n = nodes.computeIfAbsent(id, k -> new Node());
        if (labels != null) n.labels.addAll(labels);
        if (props != null) n.props.putAll(props);
    }
    public void addNode(String id){
        nodes.computeIfAbsent(id, k -> new Node());
    }
    public boolean hasNode(String id){ return nodes.containsKey(id); }

    public void addEdge(String src, String rel, String dst, Map<String,Object> props){
        addNode(src); addNode(dst);
        adj.computeIfAbsent(src, k->new HashMap<>())
           .computeIfAbsent(rel, k->new ArrayList<>())
           .add(new Edge(src, rel, dst, props==null? new HashMap<>() : new HashMap<>(props)));
    }

    public List<Edge> edgesOf(String node){
        List<Edge> out = new ArrayList<>();
        Map<String, List<Edge>> m = adj.getOrDefault(node, Map.of());
        for (var lst : m.values()) out.addAll(lst);
        return out;
    }

    public List<String> listEntities(){
        return new ArrayList<>(nodes.keySet());
    }

    public List<String> bfsPath(String start, String goal, int maxDepth){
        if (!nodes.containsKey(start) || !nodes.containsKey(goal)) return null;
        Deque<List<String>> q = new ArrayDeque<>();
        Set<String> seen = new HashSet<>();
        q.add(List.of(start));
        seen.add(start);
        while(!q.isEmpty()){
            List<String> path = q.removeFirst();
            String cur = path.get(path.size()-1);
            if (cur.equals(goal)) return path;
            if (path.size() > maxDepth) continue;
            for (var e : edgesOf(cur)){
                if (!seen.contains(e.dst)){
                    seen.add(e.dst);
                    List<String> np = new ArrayList<>(path);
                    np.add(e.dst);
                    q.add(np);
                }
            }
        }
        return null;
    }
}
