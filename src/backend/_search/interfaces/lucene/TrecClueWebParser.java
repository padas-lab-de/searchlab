package org.apache.lucene.benchmark.byTask.feeds;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 *  This is a lightly modified version of the TrecGov2Parser.  The
 *  only change is that it adds "url", "inlink", "spamScore", and
 *  "PageRank" properties to the DocData.
 */

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.Properties;

/**
 * Parser for the GOV2 collection format
 */
public class TrecClueWebParser extends TrecGov2Parser {

  private static final String DATE = "Date: ";
  private static final String DATE_END = TrecContentSource.NEW_LINE;
  
  private static final String DOCHDR = "<DOCHDR>";
  private static final String TERMINATING_DOCHDR = "</DOCHDR>";

  private static final String DOCURL = "<DOCURL>";
  private static final String TERMINATING_DOCURL = "</DOCURL>";

  private static final String INLINK = "<INLINK>";
  private static final String TERMINATING_INLINK = "</INLINK>";

  private static final String SPAM = "<SPAM>";
  private static final String TERMINATING_SPAM = "</SPAM>";

  private static final String PAGERANK = "<PAGERANK>";
  private static final String TERMINATING_PAGERANK = "</PAGERANK>";

  @Override
  public DocData parse(DocData docData, String name, TrecContentSource trecSrc, 
      StringBuilder docBuf, ParsePathType pathType) throws IOException {
    // skip some of the non-html text, optionally set date
    Date date = null;
    int start = 0;
    int h1;
    String urlStr;
    String inlinkStr;
    String spamStr;
    String pageRankStr;

    h1 = docBuf.indexOf(DOCURL);
    if (h1 >= 0) {
      final int h2 = docBuf.indexOf(TERMINATING_DOCURL, h1);
      urlStr = docBuf.substring (h1 + DOCURL.length(), h2).trim();
    } else {
      urlStr = new String();
    };

    h1 = docBuf.indexOf(INLINK);
    if (h1 >= 0) {
      final int h2 = docBuf.indexOf(TERMINATING_INLINK, h1);
      inlinkStr = docBuf.substring (h1 + INLINK.length(), h2).trim();
    } else {
      inlinkStr = new String();
    };

    h1 = docBuf.indexOf(SPAM);
    if (h1 >= 0) {
      final int h2 = docBuf.indexOf(TERMINATING_SPAM, h1);
      spamStr = docBuf.substring (h1 + SPAM.length(), h2).trim();
    } else {
      spamStr = new String("0.0");
    };

    h1 = docBuf.indexOf(PAGERANK);
    if (h1 >= 0) {
      final int h2 = docBuf.indexOf(TERMINATING_PAGERANK, h1);
      pageRankStr = docBuf.substring (h1 + PAGERANK.length(), h2).trim();
    } else {
      pageRankStr = new String("-2.0");
    };

    h1 = docBuf.indexOf(DOCHDR);
    if (h1 >= 0) {
      final int h2 = docBuf.indexOf(TERMINATING_DOCHDR, h1);
      final String dateStr = extract(docBuf, DATE, DATE_END, h2, null);
      if (dateStr != null) {
        date = trecSrc.parseDate(dateStr);
      }
      start = h2 + TERMINATING_DOCHDR.length();
    }
    final String html = docBuf.substring(start);
    docData = trecSrc.getHtmlParser().parse(docData, name, date, new StringReader(html), trecSrc);

    /*
     *  getHtmlParser creates the Properties object, so the url and
     *  inlink properties can't be stored until that completes.
     */
    docData.getProps().setProperty ("url", urlStr);
    docData.getProps().setProperty ("inlink", inlinkStr);
    docData.getProps().setProperty ("spamScore", spamStr);
    docData.getProps().setProperty ("PageRank", pageRankStr);

    return docData;
  }
  
}
