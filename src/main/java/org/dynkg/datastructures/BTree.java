package org.dynkg.datastructures;

import java.util.*;

public class BTree<K extends Comparable<K>, V> {
    private static class Node<K extends Comparable<K>, V> {
        final int t;
        boolean leaf;
        List<K> keys = new ArrayList<>();
        List<V> values = new ArrayList<>();
        List<Node<K,V>> children = new ArrayList<>();
        Node(int t, boolean leaf){ this.t=t; this.leaf=leaf; }
    }

    private final int t;
    private Node<K,V> root;

    public BTree(int t){
        if (t < 2) throw new IllegalArgumentException("t>=2");
        this.t = t;
        this.root = new Node<>(t, true);
    }

    public V search(K k){ return search(root, k); }
    private V search(Node<K,V> x, K k){
        int i=0;
        while (i < x.keys.size() && k.compareTo(x.keys.get(i)) > 0) i++;
        if (i < x.keys.size() && k.compareTo(x.keys.get(i))==0) return x.values.get(i);
        if (x.leaf) return null;
        return search(x.children.get(i), k);
    }

    public void insert(K k, V v){
        Node<K,V> r = root;
        if (r.keys.size() == 2*t-1){
            Node<K,V> s = new Node<>(t, false);
            s.children.add(r);
            root = s;
            splitChild(s, 0);
            insertNonFull(s, k, v);
        }else insertNonFull(r, k, v);
    }

    private void splitChild(Node<K,V> x, int i){
        Node<K,V> y = x.children.get(i);
        Node<K,V> z = new Node<>(t, y.leaf);
        K midK = y.keys.get(t-1);
        V midV = y.values.get(t-1);
        for(int j=t;j<y.keys.size();j++){ z.keys.add(y.keys.get(j)); z.values.add(y.values.get(j)); }
        y.keys = new ArrayList<>(y.keys.subList(0, t-1));
        y.values = new ArrayList<>(y.values.subList(0, t-1));
        if (!y.leaf){
            for(int j=t;j<y.children.size();j++){ z.children.add(y.children.get(j)); }
            y.children = new ArrayList<>(y.children.subList(0, t));
        }
        x.children.add(i+1, z);
        x.keys.add(i, midK);
        x.values.add(i, midV);
    }

    private void insertNonFull(Node<K,V> x, K k, V v){
        int i = x.keys.size()-1;
        if (x.leaf){
            x.keys.add(null); x.values.add(null);
            while(i>=0 && k.compareTo(x.keys.get(i))<0){
                x.keys.set(i+1, x.keys.get(i));
                x.values.set(i+1, x.values.get(i));
                i--;
            }
            x.keys.set(i+1, k);
            x.values.set(i+1, v);
        }else{
            while(i>=0 && k.compareTo(x.keys.get(i))<0) i--;
            i++;
            if (x.children.get(i).keys.size() == 2*t-1){
                splitChild(x, i);
                if (k.compareTo(x.keys.get(i))>0) i++;
            }
            insertNonFull(x.children.get(i), k, v);
        }
    }

    public List<Map.Entry<K,V>> inorder(){
        List<Map.Entry<K,V>> res = new ArrayList<>();
        inorder(root, res);
        return res;
    }
    private void inorder(Node<K,V> x, List<Map.Entry<K,V>> res){
        if (x.leaf){
            for(int i=0;i<x.keys.size();i++) res.add(Map.entry(x.keys.get(i), x.values.get(i)));
        }else{
            for(int i=0;i<x.keys.size();i++){
                inorder(x.children.get(i), res);
                res.add(Map.entry(x.keys.get(i), x.values.get(i)));
            }
            inorder(x.children.get(x.children.size()-1), res);
        }
    }
}
