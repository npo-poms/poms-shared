/**
 * Copyright (C) 2010 All rights reserved VPRO The Netherlands
 */
package nl.vpro.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

@Deprecated
/* Use DutchAnalyzer instead */
public final class SimpleEntityAnalyser extends Analyzer {


    @Override
    protected TokenStreamComponents createComponents(String fieldName) {

        return new TokenStreamComponents(new StandardTokenizer()) {

        };

    }

/*
    protected TokenStream tokenStream(String fieldName, Reader reader) {
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
*/
}
