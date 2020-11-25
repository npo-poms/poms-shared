/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.tva.saxon.extension;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.*;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.classification.Term;
import nl.vpro.domain.media.MediaClassificationService;
import nl.vpro.domain.media.MisGenreType;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public class MisGenreFunction extends ExtensionFunctionDefinition {
    @Override
    public StructuredQName getFunctionQName() {
        return new StructuredQName("vpro", SaxonConfiguration.VPRO_URN, "transformMisGenres");
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[]{SequenceType.NODE_SEQUENCE};
    }

    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.ANY_SEQUENCE;
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {
            @SuppressWarnings("unchecked")
            @Override
            public Sequence<StringValue> call(XPathContext context, Sequence[] arguments) throws XPathException {
                List<StringValue> result = new ArrayList<>();
                List<MisGenreType> misTypes = new ArrayList<>();

                SequenceIterator<Item<StringValue>> iterate = arguments[0].iterate();
                Item<StringValue> item = iterate.next();
                while(item != null) {
                    CharSequence misInput = item.getStringValueCS();
                    item = iterate.next();

                    if(StringUtils.isBlank(misInput)) {
                        continue;
                    }

                    MisGenreType misGenreType = toMisGenreType(misInput.toString());
                    if(misGenreType != null) {
                        misTypes.add(misGenreType);
                    }

                }

                List<Term> terms = MediaClassificationService.getTermsByMisGenreType(misTypes);
                for(Term term : terms) {
                    result.add(new StringValue(term.getTermId()));
                }
                return new SequenceExtent<>(result);
            }
        };
    }

    @Nullable
    private MisGenreType toMisGenreType(String misInput) {
        switch(misInput.toUpperCase()) {
            case "AMUSEMENT":
                return MisGenreType.ENTERTAINMENT;
            case "ANIMATIE":
                return MisGenreType.CARTOON;
            case "COMEDY":
                return MisGenreType.COMEDY;
            case "DOCUMENTAIRE":
                return MisGenreType.DOCUMENTARY;
            case "EDUCATIEF":
                return MisGenreType.EDUCATION;
            case "EROTIEK":
                return MisGenreType.EROTICA;
            case "FILM":
                return MisGenreType.MOVIE;
            case "INFORMATIEF":
                return MisGenreType.INFORMATIVE;
            case "JEUGD":
                return MisGenreType.YOUTH;
            case "KUNST/CULTUUR":
                return MisGenreType.ART_CULTURE;
            case "MISDAAD":
                return MisGenreType.CRIME;
            case "MUZIEK":
                return MisGenreType.MUSIC;
            case "NATUUR":
                return MisGenreType.NATURE;
            case "NIEUWS/ACTUALITEITEN":
                return MisGenreType.NEWS;
            case "OVERIGE":
                return MisGenreType.OTHER;
            case "RELIGIEUS":
                return MisGenreType.RELIGIOUS;
            case "SERIE/SOAP":
                return MisGenreType.SERIES_SOAP;
            case "SPORT":
                return MisGenreType.SPORT;
            case "WETENSCHAP":
                return MisGenreType.SCIENCE;
            default:
                return null;
        }
    }
}
