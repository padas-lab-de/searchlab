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
package org.apache.lucene.analysis.en;

/*
 *  This is a copy of EnglishAnalyzer class with additional
 *  configuration methods.
 */

import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.*;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * {@link Analyzer} for English.
 *
 * @since 3.1
 */
public final class EnglishAnalyzerConfigurable extends StopwordAnalyzerBase {

  public enum StemmerType { NONE, PORTER, KSTEM };

    /** An unmodifiable set containing some common English words that are not usually useful
   for searching.*/
  public static final CharArraySet ENGLISH_STOP_WORDS_SET;

  private Boolean doLowerCase=true;
  private Boolean doStopwordRemoval=true;
  private StemmerType stemmer=StemmerType.PORTER;

  static {
    final List<String> stopWords = Arrays.asList(
        "a", "an", "and", "are", "as", "at", "be", "but", "by",
        "for", "if", "in", "into", "is", "it",
        "no", "not", "of", "on", "or", "such",
        "that", "the", "their", "then", "there", "these",
        "they", "this", "to", "was", "will", "with"
    );
    final CharArraySet stopSet = new CharArraySet(stopWords, false);
    ENGLISH_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
  }

  private final CharArraySet stemExclusionSet;
   
  /**
   * Returns an unmodifiable instance of the default stop words set.
   * @return default stop words set.
   */
  public static CharArraySet getDefaultStopSet(){
    return ENGLISH_STOP_WORDS_SET;
  }

  /**
   * Builds an analyzer with the default stop words: {@link #getDefaultStopSet}.
   */
  public EnglishAnalyzerConfigurable() {
    this(ENGLISH_STOP_WORDS_SET);
  }
  
  /**
   * Builds an analyzer with the given stop words.
   * 
   * @param stopwords a stopword set
   */
  public EnglishAnalyzerConfigurable(CharArraySet stopwords) {
    this(stopwords, CharArraySet.EMPTY_SET);
  }

  /**
   * Builds an analyzer with the given stop words. If a non-empty stem exclusion set is
   * provided this analyzer will add a {@link SetKeywordMarkerFilter} before
   * stemming.
   * 
   * @param stopwords a stopword set
   * @param stemExclusionSet a set of terms not to be stemmed
   */
  public EnglishAnalyzerConfigurable(CharArraySet stopwords, CharArraySet stemExclusionSet) {
    super(stopwords);
    this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionSet));
  }

  /**
   * Creates a
   * {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}
   * which tokenizes all the text in the provided {@link Reader}.
   * 
   * @return A
   *         {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}
   *         built from an {@link StandardTokenizer} filtered with
   *         {@link EnglishPossessiveFilter},
   *         {@link LowerCaseFilter}, {@link StopFilter}
   *         , {@link SetKeywordMarkerFilter} if a stem exclusion set is
   *         provided and {@link PorterStemFilter}.
   */
  @Override
  protected TokenStreamComponents createComponents(String fieldName) {
    final Tokenizer source = new StandardTokenizer();
    TokenStream result = new EnglishPossessiveFilter(source);

    if (this.doLowerCase)
      result = new LowerCaseFilter(result);

    if (this.doStopwordRemoval)
      result = new StopFilter(result, stopwords);

    if(!stemExclusionSet.isEmpty())
      result = new SetKeywordMarkerFilter(result, stemExclusionSet);

    if (this.stemmer == StemmerType.PORTER)
      result = new PorterStemFilter(result);
    else if (this.stemmer == StemmerType.KSTEM)
      result = new KStemFilter(result);

    return new TokenStreamComponents(source, result);
  }

  @Override
  protected TokenStream normalize(String fieldName, TokenStream in) {
    return new LowerCaseFilter(in);
  }

  /**
   * Enable or disable the conversion of text to lower case.
   */
  public void setLowercase (Boolean onOff) { this.doLowerCase = onOff; }

  /**
   * Enable or disable stopword removal.
   */
  public void setStopwordRemoval (Boolean onOff) { this.doStopwordRemoval = onOff; }

  /**
   * Control whether and how stemming is done.  See StemmerType.
   */
  public void setStemmer (StemmerType s) { this.stemmer = s; }
}
