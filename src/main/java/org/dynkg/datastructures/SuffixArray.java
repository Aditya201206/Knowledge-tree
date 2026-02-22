package org.dynkg.datastructures;

import java.util.*;

public class SuffixArray {
    private final String text;
    private final int[] sa;

    public SuffixArray(String text){
        this.text = text;
        Integer[] idx = new Integer[text.length()];
        for(int i=0;i<idx.length;i++) idx[i]=i;
        Arrays.sort(idx, Comparator.comparingInt(i -> 0)); // placeholder
        Arrays.sort(idx, Comparator.comparingInt((Integer i) -> text.substring(i).hashCode()));
        // To keep deterministic, fallback to string compare:
        Arrays.sort(idx, Comparator.comparing(i -> text.substring(i)));
        sa = new int[idx.length];
        for(int i=0;i<idx.length;i++) sa[i] = idx[i];
    }

    public List<Integer> find(String pattern){
        int l = 0, r = sa.length;
        while (l<r){
            int m = (l+r)/2;
            String s = text.substring(sa[m]);
            if (s.compareTo(pattern) < 0) l = m+1; else r = m;
        }
        int start = l;
        l = start; r = sa.length;
        String hi = pattern.isEmpty()? pattern : pattern.substring(0, pattern.length()-1) + (char)(pattern.charAt(pattern.length()-1)+1);
        while (l<r){
            int m = (l+r)/2;
            String s = text.substring(sa[m]);
            if (s.compareTo(hi) < 0) l = m+1; else r = m;
        }
        int end = l;
        List<Integer> out = new ArrayList<>();
        for(int i=start;i<end;i++){
            if (text.startsWith(pattern, sa[i])) out.add(sa[i]);
        }
        return out;
    }

    public List<String> frequentPhrases(int minLen, int minCount, int maxLen, int limit){
        Map<String,Integer> cnt = new HashMap<>();
        int n = text.length();
        for(int i=0;i<=n-minLen;i++){
            String seed = text.substring(i, i+minLen);
            if (find(seed).size() >= minCount){
                int j=i+minLen;
                while (j<n && j-i < maxLen && find(text.substring(i,j)).size() >= minCount){
                    j++;
                }
                String cand = text.substring(i, j-1);
                if (cand.length() >= minLen){
                    cnt.put(cand, cnt.getOrDefault(cand, 0)+1);
                }
            }
            if (cnt.size() > limit*5) break;
        }
        return cnt.entrySet().stream()
            .sorted((a,b)->Integer.compare(b.getValue(), a.getValue()))
            .limit(limit)
            .map(Map.Entry::getKey)
            .toList();
    }
}
