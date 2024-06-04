import argparse
import os
from typing import OrderedDict

from tqdm import tqdm

from pyserini.encode import PcaEncoder
from pyserini.search.lucene import LuceneSearcher
from pyserini.analysis import JDefaultEnglishAnalyzer

import time

from flask import Flask, request
from flask import jsonify

app = Flask(__name__)

searcher = None

def define_dsearch_args(parser):
    parser.add_argument('--index', type=str, metavar='path to index or index name', required=True,
                        help="Path to BM25 index")
    parser.add_argument('--stemmer', type=str, metavar='which stemmer to use. default is kstem',
                        required=False,
                        default="krovetz",
                        help='which stemmer to use')
    parser.add_argument('--k1', type=float, required=False, default=1.2, help='BM25 k1 parameter.')
    parser.add_argument('--b', type=float, required=False, default=0.75, help='BM25 b parameter.')                                              

@app.route('/search', methods=['GET'])
def search():

    args = request.args
    query = args.get("query")

    if query is None:
        return "Missing query", 400

    hits = args.get("hits")

    if hits:
        try:
            hits = int(hits)
        except:
            hits=1000
    else:
        hits=1000

    single_query_start = time.time()

    if searcher is None:
        return "Searcher not initialized", 503

    results = searcher.search(query, k=hits, fields={'body':1.0})
    single_query_search_end = time.time()

    print ("Done in "+str(single_query_search_end-single_query_start)+" seconds")

    response = {"results": [{"docid": str(result.docid), \
                            "score": float(result.score), \
                            "url": str(result.lucene_document.get('rawUrl')), \
                            "body": str(result.lucene_document.get('body-string'))} \
                            for result in results]}

    return jsonify(response)


if __name__ == '__main__':

    parser = argparse.ArgumentParser(description='Search a BM25 index.')

    define_dsearch_args(parser)
    args = parser.parse_args()

    searcher_init_start = time.time()

    if os.path.exists(args.index):
        searcher = LuceneSearcher(args.index)
    else:
        raise ValueError("Index path does not exist")

    searcher.set_bm25(args.k1, args.b)
    analyzer = JDefaultEnglishAnalyzer.fromArguments(args.stemmer, False, None)
    searcher.set_analyzer(analyzer)

    searcher_init_end = time.time()

    print ("Searcher initialized in "+str(searcher_init_end-searcher_init_start)+" seconds")

    if not searcher:
        exit()

    app.run(host='0.0.0.0', port=7001)
     