package nl.vpro.domain.convert;

import java.util.function.BooleanSupplier;

public class TestResult<O> implements BooleanSupplier {

    @Override
    public boolean getAsBoolean() {
        return test == MatchResult.MATCH || test == MatchResult.MATCH_AND_STOP;
    }

    public enum MatchResult {
        MATCH,
        NO_MATCH,
        MATCH_AND_STOP;
    }


    final MatchResult test;
    final O object;

    public TestResult(MatchResult test, O object) {
        this.test = test;
        this.object = object;
    }

    public TestResult(boolean test, O object) {
        this.test = test ? MatchResult.MATCH : MatchResult.NO_MATCH;
        this.object = object;
    }

    public MatchResult test() {
        return test;
    }

    public O object() {
        return object;
    }
}
