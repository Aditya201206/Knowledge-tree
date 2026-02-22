package org.dynkg.ingestion;

import org.dynkg.datastructures.SuffixArray;
import java.util.*;

public class PhraseMining {
    public static List<String> minePhrases(String text, int minLen, int minCount, int limit){
        SuffixArray sa = new SuffixArray(text);
        return sa.frequentPhrases(minLen, minCount, 50, limit);
    }
}
