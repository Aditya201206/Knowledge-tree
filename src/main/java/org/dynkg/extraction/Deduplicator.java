package org.dynkg.extraction;

import org.dynkg.datastructures.BloomFilter;
import java.util.*;

public class Deduplicator {
    private final BloomFilter bloom = new BloomFilter(50000, 0.01);

    public List<ERE.Triple> filterNew(List<ERE.Triple> triples){
        List<ERE.Triple> out = new ArrayList<>();
        for (var tr : triples){
            String key = tr.key();
            if (!bloom.mightContain(key)){
                bloom.add(key);
                out.add(tr);
            }
        }
        return out;
    }
}
