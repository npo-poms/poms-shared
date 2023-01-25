package nl.vpro.domain.convert;

public class TestResult<O> {
    final boolean test;
    final O object;

    public TestResult(boolean test, O object) {
        this.test = test;
        this.object = object;
    }

    public boolean test() {
        return test;
    }

    public O object() {
        return object;
    }
}
