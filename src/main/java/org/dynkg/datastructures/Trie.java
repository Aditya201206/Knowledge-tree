package org.dynkg.datastructures;

import java.util.*;

public class Trie {
    static class Node {
        Map<Character, Node> ch = new HashMap<>();
        boolean end = false;
    }
    private final Node root = new Node();

    public void insert(String s){
        if (s == null) return;
        s = s.trim().toLowerCase();
        Node cur = root;
        for(char c : s.toCharArray()){
            cur = cur.ch.computeIfAbsent(c, k->new Node());
        }
        cur.end = true;
    }

    public boolean contains(String s){
        if (s == null) return false;
        s = s.trim().toLowerCase();
        Node cur = root;
        for(char c : s.toCharArray()){
            Node nxt = cur.ch.get(c);
            if (nxt == null) return false;
            cur = nxt;
        }
        return cur.end;
    }

    /** Longest match length starting at startIdx in text */
    public int longestMatchFrom(String text, int startIdx){
        Node cur = root;
        int lastEnd = -1;
        for(int i=startIdx;i<text.length();i++){
            char c = Character.toLowerCase(text.charAt(i));
            Node nxt = cur.ch.get(c);
            if (nxt == null) break;
            cur = nxt;
            if (cur.end) lastEnd = i;
        }
        return lastEnd >= 0 ? lastEnd - startIdx + 1 : 0;
    }

    public List<String> suggest(String prefix, int limit){
        prefix = prefix.toLowerCase();
        Node cur = root;
        for(char c: prefix.toCharArray()){
            Node nxt = cur.ch.get(c);
            if (nxt == null) return List.of();
            cur = nxt;
        }
        List<String> out = new ArrayList<>();
        dfs(cur, new StringBuilder(prefix), out, limit);
        return out;
    }

    private void dfs(Node node, StringBuilder sb, List<String> out, int limit){
        if (out.size() >= limit) return;
        if (node.end) out.add(sb.toString());
        for (var e : node.ch.entrySet()){
            sb.append(e.getKey());
            dfs(e.getValue(), sb, out, limit);
            sb.setLength(sb.length()-1);
        }
    }
}
