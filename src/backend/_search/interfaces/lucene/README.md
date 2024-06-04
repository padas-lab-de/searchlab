# Lucene indexing and BM25 search

The `lucene` directory contains all relevant code to create Lucene indexes and run BM25 search on them.

## Indexing
We use Java coda in `IndexTrec.java` to index ClueWeb22-B English documents. Indexing is done in 4 shards comrpising parts 0-11, 12-23, 24-35, and 36-46 of the corpus. To begin indexing, navigate to the `lucene` directory of the repository and run `make` to compile the Java code and dependencies. After this, run the bash scripts `doIndex1.sh`, `doIndex2.sh`, `doIndex3.sh`, and `doIndex4.sh` to run slurm jobs that will build the 4 indexes.

Currently, these are set to run on the nodes 2-7, 2-8, 2-9, and 2-10, however the indexes are saved in the shared memory. Modify the variable `index_dir` in the scripts to specify the index paths (preferably SSDs on the nodes running the job). Modify line nmber 6 (`#SBATCH --nodelist=...`) in the scripts to specify the nodes to run the jobs on.

Modify the `-stem` argument inside the scripts to specify the type of stemming. It can either be `kstem`, `porter`, or `none`.

The `-docs` argument inside the scripts has a comma separated list of the directories corresponding to the parts of the corpus to be indexed.

## BM25 search
Once the indexes are built use the following code in a shell script to run search on one index:

```
qstem=krovetz
k1=1.2
b=0.75
hits=1000

qnum=3k

DATA_DIR=/bos/tmp2/hmehrotr

OUTPUT_DIR=/bos/tmp2/hmehrotr/CW22_bm25_res/bm25-${qstem}-${k1}-${b}

mkdir -p $OUTPUT_DIR

sbatch -n 12 -N 1 --mem=90000 --time=24:00:00 --nodelist=boston-2-7 \
  --wrap="/bos/usr0/zhenfan/anserini_2023/target/appassembler/bin/SearchCollection \
  -threads 6 \
  -index ${DATA_DIR}/CW22_indexes/ind_en00_00_11_v3 \
  -fields body=1.0 \
  -topicreader TsvString \
  -topics ${DATA_DIR}/cw22b_en_search/query_${qnum}.tsv \
  -output ${OUTPUT_DIR}/query_${qnum}_00_11.tsv \
  -bm25 -bm25.k1 $k1 -bm25.b $b -hits $hits -stemmer $qstem "
```

A bried description of the most important arguments above:

1. `qstem`: The type of stemmer. Should match the stemmer used while indexing. Use `krovetz` for kstem stemming and `porter` for porter stemming.
1. `k1`, `b`: BM25 parameters
1. `hits`: Number of results to get for each query
1. `DATA_DIR`: Directory with data like this repository, a subdirectory called `CW22_indexes` with indexes in it. Modify according to your setup.
1. `--nodelist`: Node to run the search job on. Important to specify if the index resides a certain node's SSD.
1. `-index`: Path to the index to search on. This example uses the first index shard.
1. `-topics`: Path to the queries file. This example used the file in this repository cloned in `DATA_DIR`.

This will generate a results file in TREC format.

## BM25 search service
You can run a Flask service with a search endpoint to perform BM25 search on an index. Navigate to the `lucene` directory of the repository and run the following command:

```
sbatch -n 8 -N 1 --mem=80G --time=0 --nodelist=boston-2-7 \
  --wrap="python bm25_service.py   --index /ssd/CW22b_bm25_v3_00_11   --stemmer krovetz --k1 1.2 --b 0.75 "
```

This service uses Pyserini to perform BM25 search. A brief description of the arguments used above:

1. `--nodelist`: The node on which the service will run. If using an index on a certail node's SSD, this argument should be set to that node.
1. `--index`: Path of the index inside a node or in shared memory.
1. `--stemmer`: The type of stemmer. Should match the stemmer used while indexing. Use `krovetz` for kstem stemming and `porter` for porter stemming.
1. `--k1`, `--b`: BM25 parameters

Similarly, 4 services can be run to search and return results from the 4 indexes. Now, from the first few lines of the slurm log file of the job running the service, one can check the IP and port of the service. If the IP is `10.1.1.28` and port `7001` then the API URL for the query "Who can introduce new bills" would be:

`http://10.1.1.28:7001/search?query=Who+can+introduce+new+bills&hits=1000`

Here the argument `hits` specifies the number of results to obtain from BM25 search. To test this API from command line from the boston cluster, run:

`curl --request GET "http://10.1.1.28:7001/search?query=Who+can+introduce+new+bills&hits=1000"`

The output is a JSON with a single key `results`. The value is an array of `result` objects in decreasing order of BM25 scores. Each `result` object has the following:

1. `docid`: String document id
1. `score`: Float BM25 score
1. `url`: The raw URL of the document
1. `body`: The full text of the document

So, for 2 results, the output JSON would look like:

```
{
  "results": [
    {
      "docid": "ABCD1234",
      "score": 0.56,
      "url": "https://www.wikipedia.com",
      "body": "title1\nbody1"
    },
    {
      "docid": "EFGH1234",
      "score": 0.40,
      "url": "https://www.google.com",
      "body": "title2\nbody2"
    }
  ]
}
```

