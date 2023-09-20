/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.tva.saxon.extension;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.*;
import net.sf.saxon.tree.iter.ListIterator;
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
            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) {
                List<AtomicValue> result = new ArrayList<>();
                List<MisGenreType> misTypes = new ArrayList<>();

                try (SequenceIterator iterate = arguments[0].iterate()) {
                    Item item = iterate.next();
                    while(item != null) {
                        CharSequence misInput = item.getStringValue();
                        item = iterate.next();

                        if (StringUtils.isBlank(misInput)) {
                            continue;
                        }

                        MisGenreType misGenreType = toMisGenreType(misInput.toString());
                        if (misGenreType != null) {
                            misTypes.add(misGenreType);
                        }
                    }
                }

                List<Term> terms = MediaClassificationService.getTermsByMisGenreType(misTypes);
                for(Term term : terms) {
                    result.add(new StringValue(term.getTermId()));
                }
                return SequenceExtent.from(new ListIterator.OfAtomic<>(result));
            }
        };
    }

    @Nullable
    private MisGenreType toMisGenreType(String misInput) {
        return switch (misInput.toUpperCase()) {
            case "AMUSEMENT" -> MisGenreType.ENTERTAINMENT;
            case "ANIMATIE" -> MisGenreType.CARTOON;
            case "COMEDY" -> MisGenreType.COMEDY;
            case "DOCUMENTAIRE" -> MisGenreType.DOCUMENTARY;
            case "EDUCATIEF" -> MisGenreType.EDUCATION;
            case "EROTIEK" -> MisGenreType.EROTICA;
            case "FILM" -> MisGenreType.MOVIE;
            case "INFORMATIEF" -> MisGenreType.INFORMATIVE;
            case "JEUGD" -> MisGenreType.YOUTH;
            case "KUNST/CULTUUR" -> MisGenreType.ART_CULTURE;
            case "MISDAAD" -> MisGenreType.CRIME;
            case "MUZIEK" -> MisGenreType.MUSIC;
            case "NATUUR" -> MisGenreType.NATURE;
            case "NIEUWS/ACTUALITEITEN" -> MisGenreType.NEWS;
            case "OVERIGE" -> MisGenreType.OTHER;
            case "RELIGIEUS" -> MisGenreType.RELIGIOUS;
            case "SERIE/SOAP" -> MisGenreType.SERIES_SOAP;
            case "SPORT" -> MisGenreType.SPORT;
            case "WETENSCHAP" -> MisGenreType.SCIENCE;
            default -> null;
        };
    }
}
