#  Dynamic Knowledge Graph – Java Maven Project

> **Advanced semantic graph engine** combining 5 powerful data structures to build, version, and query evolving knowledge networks.  
> _Extract entities, mine phrases, track temporal versions, and answer natural-language-like queries._

---

##  Overview

This project implements a **minimal yet production-grade knowledge graph prototype** in **Java 17** using **Maven**. It ingests unstructured text, discovers entities and relationships, stores them in a versioned property graph, and exposes a rich query interface. The core innovation lies in the **fusion of five classical data structures** to achieve high-performance extraction, deduplication, versioning, and retrieval.

Whether you're building a **question‑answering system**, a **research knowledge base**, or a **temporal information tracker**, this engine provides the foundational layers.

---

##  Architectural Pillars – 5 Data Structures

| Structure       | Role                                                                 | Location |
|-----------------|----------------------------------------------------------------------|----------|
| **Trie**        | Named Entity Recognition (NER) & real‑time autocomplete              | `NER module` |
| **Suffix Array**| Phrase mining / keyphrase extraction without external dictionaries   | `Mining module` |
| **Bloom Filter**| Fast deduplication of candidate triples during ingestion             | `Deduplication layer` |
| **B‑Tree**      | Temporal versioning – maps commit timestamps to graph snapshots      | `Versioning module` |
| **Property Graph** (adjacency list) | Stores entities, relations, attributes, and supports path traversal | `Core graph storage` |

These structures work in concert:
1. **Ingestion** → tokenization + phrase mining (Suffix Array) + entity recognition (Trie) → candidate triples.
2. **Bloom Filter** eliminates duplicates before storage.
3. **Versioning** (B‑Tree) keeps a full history of graph states.
4. **Property Graph** hosts the live knowledge with efficient adjacency lookups.
5. **Query layer** leverages graph traversals (BFS/DFS) for entity/relation/path/temporal queries.

---

##  Features

-  **Named Entity Recognition** – pre‑loaded dictionary + Trie for instant lookup & prefix suggestions.
- **Phrase Mining** – suffix array automatically discovers salient multi‑word terms from raw text.
-  **Deduplication** – Bloom filter prevents duplicate `(subject, predicate, object)` triples.
-  **Temporal Versioning** – B‑Tree stores every commit; query any past state by timestamp.
-  **Property Graph Core** – entities have properties; edges support labels & weights.
-  **Rich Query API**:
  - `entities(prefix)` – autocomplete entity names.
  - `relationsOf(entity)` – all outgoing/incoming edges.
  - `path(source, target)` – shortest path using BFS.
  - `temporalQuery(timestamp, relation)` – retrieve versioned facts.

---

##  Project Structure
dynamic-knowledge-graph/
├── pom.xml # Maven config (Java 17)
├── src/main/java/com/dkg/
│ ├── core/
│ │ ├── GraphStore.java # Property graph adjacency + BTree versioning
│ │ ├── Trie.java # NER & autocomplete
│ │ ├── SuffixArray.java # Phrase mining
│ │ ├── BloomFilter.java # Duplicate elimination
│ │ └── BTree.java # Timestamp → Graph version
│ ├── ingestion/
│ │ └── Pipeline.java # Text ingestion → triples
│ ├── query/
│ │ └── QueryEngine.java # Query interfaces
│ └── Main.java # CLI runner
├── data/
│ ├── dictionary.txt # Seed entities (one per line)
│ ├── sample.txt # Input corpus
│ └── graph.json # Serialized graph output
└── README.md



---

##  Prerequisites

- **Java 17** (or higher)
- **Maven 3.6+**

---

##  Setup & Build

1. **Clone the repository**
   ```bash
   git clone https://github.com/MANIDEEP2407-SYS/Dynamic-Knowledge-Graph.git
   cd Dynamic-Knowledge-Graph
Compile and package

bash
mvn clean compile
Run with default files

bash
mvn -q -e -DskipTests exec:java
Run with custom files

bash
mvn -q -DskipTests exec:java -Dexec.args="--dictionary data/custom_dict.txt --input data/corpus.txt --save data/output.json"
 Usage Examples
Basic ingestion & query (from Main.java)
java
// 1. Load dictionary and input
GraphStore graph = new GraphStore();
graph.loadDictionary("data/dictionary.txt");
graph.ingest("data/sample.txt");   // runs phrase mining, entity recognition, triple extraction

// 2. Query: autocomplete entities starting with "Al"
List<String> entities = graph.queryEngine().entities("Al");
// → ["Albert Einstein", "Alan Turing", ...]

// 3. Get all relations of "Albert Einstein"
List<Relation> rels = graph.queryEngine().relationsOf("Albert Einstein");
// → [(Einstein) -[worked_at]-> (Princeton), (Einstein) -[discovered]-> (Photoelectric Effect), ...]

// 4. Shortest path between "Einstein" and "Quantum Mechanics"
List<String> path = graph.queryEngine().path("Albert Einstein", "Quantum Mechanics");
// → ["Albert Einstein", "Photoelectric Effect", "Quantum Mechanics"]

// 5. Temporal query: facts that existed at timestamp 1672531200000
List<Relation> pastFacts = graph.queryEngine().temporalQuery(1672531200000L, "discovered");
Command-line output (example)
text
[INFO] Loading dictionary... 152 entries loaded.
[INFO] Building suffix array for phrase mining... done.
[INFO] Mined phrases: ["machine learning", "neural network", "knowledge graph"]
[INFO] Extracted triples:
  (machine learning) -[is_a]-> (AI subfield)
  (neural network) -[uses]-> (backpropagation)
  (knowledge graph) -[stores]-> (facts)
[INFO] Bloom filter prevented 3 duplicates.
[INFO] Committed version at timestamp 1712592000000.
[INFO] Graph saved to data/graph.json.

Queries:
> entities("know") → ["knowledge graph", "knowledge base"]
> relationsOf("neural network") → [(neural network)-[uses]->(backpropagation)]
> path("machine learning", "backpropagation") → ["machine learning", "neural network", "backpropagation"]
> temporal (older timestamp) → returns versioned relations.
 File Formats
Dictionary (dictionary.txt)
One entity per line. Used to populate the Trie for NER.


Albert Einstein
Machine Learning
Python
Neural Network
Input Text (sample.txt)
Raw sentences. The system mines phrases and extracts triples using co‑occurrence patterns.

text
Machine learning is a subfield of AI. Neural networks use backpropagation.
Albert Einstein discovered the photoelectric effect while working at Princeton.
Graph Export (graph.json)
JSON representation of the entire graph (entities, relations, versions).

{
  "entities": {
    "machine_learning": { "type": "concept" },
    "neural_network": { "type": "technology" }
  },
  "edges": [
    ["machine_learning", "is_a", "AI subfield", {"timestamp": 1712592000000}],
    ["neural_network", "uses", "backpropagation", {"timestamp": 1712592000001}]
  ],
  "versions": [
    {"timestamp": 1712592000000, "snapshot": "uri"}
  ]
}
🔧 Advanced Configuration
You can tweak parameters inside Pipeline.java and GraphStore.java:

Parameter	Location	Default	Description
minPhraseLength	SuffixArray	2	Minimum words for a phrase
bloomFilterSize	BloomFilter	10_000	Bitset size
maxPathDepth	QueryEngine	5	Max BFS depth for path queries
versionHistoryLimit	BTree	100	Max versions stored
