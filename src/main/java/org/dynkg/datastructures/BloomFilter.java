package org.dynkg.datastructures;

import java.security.MessageDigest;
import java.util.BitSet;

public class BloomFilter {
    private final int m;
    private final int k;
    private final BitSet bits;

    public BloomFilter(int capacity, double fpr){
        double md = - (capacity * Math.log(fpr)) / (Math.log(2)*Math.log(2));
        this.m = (int)Math.ceil(md);
        this.k = Math.max(2, (int)Math.round((m / (double)capacity) * Math.log(2)));
        this.bits = new BitSet(m);
    }

    private long[] hashes(byte[] data){
        try{
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] h1 = sha.digest(data);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] h2 = md5.digest(data);
            long a = 0;
            for(int i=0;i<8;i++){ a = (a<<8) | (h1[i] & 0xFF); }
            long b = 0;
            for(int i=0;i<8;i++){ b = (b<<8) | (h2[i] & 0xFF); }
            long[] out = new long[k];
            for(int i=0;i<k;i++){
                out[i] = Math.floorMod(a + i*b + i*i, m);
            }
            return out;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public void add(String item){
        byte[] d = item.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        for(long idx : hashes(d)){
            bits.set((int)idx);
        }
    }

    public boolean mightContain(String item){
        byte[] d = item.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        for(long idx : hashes(d)){
            if (!bits.get((int)idx)) return false;
        }
        return true;
    }
}
