package org.dynkg.query;

import org.dynkg.ingestion.DictionaryNER;
import java.util.List;

public class Autocomplete {
    private final DictionaryNER ner;
    public Autocomplete(DictionaryNER ner){ this.ner = ner; }
    public List<String> suggest(String prefix, int limit){ return ner.suggest(prefix, limit); }
}
