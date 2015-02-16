/**
 * Copyright (C) 2010 All rights reserved VPRO The Netherlands
 */
package nl.vpro.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

public class SimpleEntityAnalyser extends Analyzer {

    static final Version VERSION = Version.LUCENE_30;

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        return new ASCIIFoldingFilter(
            new LowerCaseFilter(
                VERSION,
                new StandardFilter(
                    VERSION,
                    new StandardTokenizer(LuceneHelper.VERSION, reader)
                )
            )
        );
    }
}
