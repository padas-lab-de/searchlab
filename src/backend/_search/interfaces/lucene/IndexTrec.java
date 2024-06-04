import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.en.EnglishAnalyzerConfigurable;
import org.apache.lucene.benchmark.byTask.utils.StreamUtils;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.benchmark.byTask.feeds.DocData;
import org.apache.lucene.benchmark.byTask.feeds.DocMaker;
import org.apache.lucene.benchmark.byTask.feeds.TrecContentSource;
import org.apache.lucene.benchmark.byTask.utils.Config;
import org.apache.lucene.benchmark.byTask.feeds.NoMoreDataException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

class IndexTrec {

  private static String corpusParser = new String ("org.apache.lucene.benchmark.byTask.feeds.TrecGov2ParserTweaked");
  private static Map<String, Double> pageRankValues = new HashMap<String, Double>();
  private static Map<String, Double> spamValues = new HashMap<String, Double>();

  // Lucene's max is 32K UTF-8 bytes. We use 16K characters, which is simpler.
  final private static int storedFieldsMaxChars = 16384;
  private static ArrayList<String> storedFields = new ArrayList<String>();
  private static long storedFieldsTruncated = 0;

  public static void main(String[] args) throws IOException {

    String usage = "java IndexTrec -classpath .:lucene-8.1.1/*"
      + " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
      + "where options include\n"
      + "    -lowercase [true | false]\n"
      + "    -stop [true | false]\n"
      + "    -stem [none | porter | kstem]\n"
      + "    -format [cw09 | cw22 | gov2]\n"
      + "\n"
      + "This program indexes the TREC document files in DOCS_PATH, creating a\n"
      + " Lucene index in INDEX_PATH that can be searched with SearchFiles.\n";

    EnglishAnalyzerConfigurable analyzer = new EnglishAnalyzerConfigurable ();
    String indexPath = "index";
    String docsPaths = null;
    boolean create = true;
    boolean cw22 = false;

    /*
     *  Process parameters.
     */
    for (int i=0; i<args.length; i++) {
      if (("-docs".equals(args[i])) && ((i+1) < args.length)) {
        docsPaths = args[++i];
      } else if ("-index".equals(args[i]) && ((i+1) < args.length)) {
        indexPath = args[++i];
      } else if ("-update".equals(args[i])) {
        create = false;
      } else if (("-lowercase".equals(args[i])) && ((i+1) < args.length)) {
        analyzer.setLowercase ("true".equals(args[++i]));
      } else if (("-stop".equals(args[i])) && ((i+1) < args.length)) {
        analyzer.setStopwordRemoval ("true".equals(args[++i]));
      } else if (("-stem".equals(args[i])) && ((i+1) < args.length)) {
        if ("porter".equals(args[i+1])) 
          analyzer.setStemmer (EnglishAnalyzerConfigurable.StemmerType.PORTER);
        else if ("kstem".equals(args[i+1])) 
          analyzer.setStemmer (EnglishAnalyzerConfigurable.StemmerType.KSTEM);
        else
          analyzer.setStemmer (EnglishAnalyzerConfigurable.StemmerType.NONE);
        i++;
      } else if (("-format".equals(args[i])) && ((i+1) < args.length)) {
	if ("cw09".equals(args[i+1]))
	  corpusParser = "org.apache.lucene.benchmark.byTask.feeds.TrecClueWebParser";
	else if ("gov2".equals(args[i+1]))
	  corpusParser = "org.apache.lucene.benchmark.byTask.feeds.TrecGov2ParserTweaked";
    else if ("cw22".equals(args[i+1]))
      cw22 = true;
	else {
	  System.err.println ("Error:  Unknown parser " + args[i+1]);
	  System.exit(1);
	}

        i++;
      } else if ("-storeField".equals(args[i]) && ((i+1) < args.length)) {
	  storedFields.add(args[++i]);
      } else {
        System.err.println("Usage: " + usage);
        System.exit(1);
      };
    }

    if ((docsPaths == null) || (indexPath == null)) {
      System.err.println("Usage: " + usage);
      System.exit(1);
    };

    /*
     *  Prepare to read a file of documents in TREC web format.
     */
//    TrecContentSource dcsr = new TrecContentSource ();
//    Properties pr = new Properties ();
//    pr.setProperty ("work.dir", (new File (docsPath)).getAbsolutePath());
//    pr.setProperty ("docs.dir", (new File (docsPath)).getAbsolutePath());
//    pr.setProperty ("trec.doc.parser", corpusParser);
//    pr.setProperty ("content.source.forever", "false");
//    pr.setProperty ("content.source.log.step", "100");
//    pr.setProperty ("content.source.verbose", "true");
//    pr.setProperty ("content.source.excludeIteration", "true");
//
//    Config cr = new Config (pr);
//    if (!cw22) {
//      dcsr.setConfig (cr);
//      dcsr.resetInputs();
//    }
//
//
//    DocMaker dm = new DocMaker ();
//
//    pr = new Properties ();
//    pr.setProperty ("content.source", "TrecContentSource");
//    pr.setProperty ("doc.stored", "true");
//    pr.setProperty ("doc.tokenized", "true");
//    pr.setProperty ("doc.term.vector", "true");
//    pr.setProperty ("doc.term.vector.positions", "true");
//
//    cr = new Config (pr);
//    dm.setConfig (cr, dcsr);

    /*
     *  Initialize the index.
     */
    Date start = new Date();
    try {
      System.out.println ("Indexing to directory '" + indexPath + "'...");

      Directory dir = FSDirectory.open(Paths.get (indexPath));
      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
      iwc.setOpenMode(OpenMode.CREATE);

      // Optional: for better indexing performance, if you
      // are indexing many documents, increase the RAM
      // buffer.  But if you do this, increase the max heap
      // size to the JVM (eg add -Xmx512m or -Xmx1g):
      //
      // iwc.setRAMBufferSizeMB(256.0);

      IndexWriter writer = new IndexWriter(dir, iwc);
      if (!cw22) {
//        indexDocs (writer, dcsr);
//        System.out.println (dcsr.getTotalItemsCount() + " documents indexed");
//        dcsr.close ();
      }
      else {
        ClueWeb22Parser parser = new ClueWeb22Parser(docsPaths);
        indexCW22Docs(writer, parser);
      }

      System.out.println(storedFieldsTruncated + " fields were truncated to "
			 + storedFieldsMaxChars + " characters");

      // NOTE: if you want to maximize search performance,
      // you can optionally call forceMerge here.  This can be
      // a terribly costly operation, so generally it's only
      // worth it when your index is relatively static (ie
      // you're done adding documents to it):
      //
      // writer.forceMerge(1);

      writer.close();

      //  Timing information.

      Date end = new Date();
      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

    } catch (IOException e) {
      System.out.println(" caught a " + e.getClass() +
          "\n with message: " + e.getMessage());
    }

  }

  /*
   *  Add a content (searchable) field to the Lucene index.
   *  If -storeField was specified, also add a string version
   *  of the field.
   */
  static void indexContentField (Document doc, FieldType fieldType,
				 String fieldName, String content) {

    //  Insert into the Lucene index.
    doc.add (new Field (fieldName, content, fieldType));

    //  If external field storage is enabled, remove unnecessary
    //  whitespace (because it is not helpful for neural rankers)
    //  and truncate to a length that is shorter than Lucene's
    //  maximum StringField length.
    if (storedFields.contains(fieldName)) {
	content = content.replaceAll("(?m)^\\s+", "");
	if (content.length() > storedFieldsMaxChars) {
	    content = content.substring(0, storedFieldsMaxChars);
	  storedFieldsTruncated ++;
	}
	doc.add (new StringField (fieldName + "-string", content, Field.Store.YES));
    }
  }

  /*
   *  Index the documents.  This method uses less Lucene automation
   *  than other implementations, but gives more control over how
   *  different document fields are handled.
   */
  static void indexDocs (IndexWriter writer, TrecContentSource dcsr) {

    /*
     *  Create a new fieldtype that is indexed, tokenized, and stored.
     *  This is used to control indexing of all content-oriented fields.
     */
    FieldType storedTextField = new FieldType();
    // storedTextField.setStored(true);		// Handy for debugging
    storedTextField.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    storedTextField.setStoreTermVectors(true);
    storedTextField.setStoreTermVectorPositions(true);
    storedTextField.setTokenized(true);
    storedTextField.freeze();

    /*
     *  Each pass of the loop indexes one document.  The document data
     *  is gotten from the parser, and then used to create a new
     *  document object that is indexed.
     */
    DocData d = new DocData ();

    int i=1;
    int skipped = 0;

    while (true) {
      try {
        d = dcsr.getNextDocData (d);

        Document doc = new Document();

        doc.add (new StringField ("externalId", d.getName(), Field.Store.YES));

	if (d.getDate() != null) {
	  doc.add (new StringField ("date", d.getDate(), Field.Store.YES));
	}

	indexContentField(doc, storedTextField, "title", d.getTitle());
	indexContentField(doc, storedTextField, "body", d.getBody());

        if (d.getProps().getProperty ("keywords") != null) {
	  indexContentField(doc, storedTextField, "keywords",
			    d.getProps().getProperty ("keywords"));
	}

        if (d.getProps().getProperty ("inlink") != null) {
	  indexContentField(doc, storedTextField, "inlink",
			    d.getProps().getProperty ("inlink"));
	}

        if (d.getProps().getProperty ("spamScore") != null) {
          String u = d.getProps().getProperty ("spamScore");
          doc.add (new StringField ("spamScore", u, Field.Store.YES));
        }

        if (d.getProps().getProperty ("PageRank") != null) {
          String u = d.getProps().getProperty ("PageRank");
          doc.add (new StringField ("PageRank", u, Field.Store.YES));
        }

        if (d.getProps().getProperty ("url") != null) {
          String u = d.getProps().getProperty ("url");
          doc.add (new StringField ("rawUrl", u, Field.Store.YES));

          u = u.replace ('.', ' ');
          u = u.replace ('_', ' ');

	  indexContentField(doc, storedTextField, "url", u);
        }

        /*
         *  Add the document to the index.
         */
        writer.addDocument(doc);
        i++;
      }
      catch (NoMoreDataException e) {
        break;
      }
      catch (IOException e) {
	  System.out.println("Caught IOException on doc " + d.getName()
			     + ": " + e.getMessage());
        System.out.println("Document skipped");
        //e.printStackTrace();
        //break;
        skipped++;
      };

      if ((i % 100) == 0)
        System.out.println (i + " documents...");
    };
    System.out.println("Skipped " + skipped);
  }

  static void indexCW22Docs (IndexWriter writer, ClueWeb22Parser parser) {

    /*
     *  Create a new fieldtype that is indexed, tokenized, and stored.
     *  This is used to control indexing of all content-oriented fields.
     */
    FieldType storedTextField = new FieldType();
    // storedTextField.setStored(true);		// Handy for debugging
    storedTextField.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    storedTextField.setStoreTermVectors(true);
    storedTextField.setStoreTermVectorPositions(true);
    storedTextField.setTokenized(true);
    storedTextField.freeze();

    /*
     *  Each pass of the loop indexes one document.  The document data
     *  is gotten from the parser, and then used to create a new
     *  document object that is indexed.
     */

    int i=1;
    int skipped = 0;
    DocData d = new DocData();

    while (true) {
      try {
        d = parser.getNextDocData();

        Document doc = new Document();

        doc.add (new StringField ("id", d.getName(), Field.Store.YES));
	doc.add(new BinaryDocValuesField("id", new BytesRef(d.getName())));

        indexContentField(doc, storedTextField, "title", d.getTitle());
        indexContentField(doc, storedTextField, "body", d.getBody());

        if (d.getProps().getProperty ("urlHash") != null) {
          String u = d.getProps().getProperty ("urlHash");
          doc.add (new StringField ("urlHash", u, Field.Store.YES));
        }

        if (d.getProps().getProperty ("language") != null) {
          String u = d.getProps().getProperty ("language");
          doc.add (new StringField ("language", u, Field.Store.YES));
        }

        if (d.getProps().getProperty ("url") != null) {
          String u = d.getProps().getProperty ("url");
          doc.add (new StringField ("rawUrl", u, Field.Store.YES));

          u = u.replace ('.', ' ');
          u = u.replace ('_', ' ');

          indexContentField(doc, storedTextField, "url", u);
        }

        /*
         *  Add the document to the index.
         */
        writer.addDocument(doc);
        i++;
      }
      catch (NoMoreDataException e) {
        break;
      }
      catch (IOException e) {
        System.out.println("Caught IOException on doc " + d.getName()
                + ": " + e.getMessage());
        System.out.println("Document skipped");
        //e.printStackTrace();
        //break;
        skipped++;
      };

      if ((i % 10000) == 0)
        System.out.println (i + " documents...");
    };
    System.out.println("Skipped " + skipped);
    System.out.println ((i-1) + " documents indexed");
  }

  static DocData parseCW22Doc (Path filePath) throws IOException {
    InputStream inputStream = StreamUtils.inputStream(filePath);
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1.name()), 65536);



    return null;
  }

}
