package nl.vpro.domain.api.media;

import java.util.Collections;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.Result;
import nl.vpro.domain.media.Program;

/**
 * Exists only because of https://jira.vpro.nl/browse/API-118
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlRootElement(name = "programResult")
@XmlType(name = "programResultType")
public class ProgramResult extends Result<Program> {

    public static ProgramResult emptyResult(Long offset, Integer max) {
        return new ProgramResult(Collections.emptyList(), offset, max, Total.EMPTY);
    }

    public ProgramResult() {

    }
    public ProgramResult(List<Program> programs, Long offset, Integer max, Total total) {
        super(programs,  offset, max, total);
    }

    public ProgramResult(Result<Program> programs) {
        super(programs);
    }
}
