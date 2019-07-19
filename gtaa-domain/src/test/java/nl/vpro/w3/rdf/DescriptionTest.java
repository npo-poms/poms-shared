package nl.vpro.w3.rdf;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DescriptionTest {

    private Description d;

    @Before
    public void init() {
        d = new Description();
    }

    @Test
    public void hasRedirectedFrom_null() {
        addNote(null);
        assertThat(d.hasRedirectedFrom()).isFalse();
    }

    @Test
    public void hasRedirectedFrom_empty() {
        addNote("");
        assertThat(d.hasRedirectedFrom()).isFalse();
    }

    @Test
    public void hasRedirectedFrom_Exists() {
        addNote("Forward:asdas");
        assertThat(d.hasRedirectedFrom()).isTrue();
    }

    @Test
    public void hasRedirectedFrom_ExistsSpaces() {
        addNote("Forward: \n somethign");
        assertThat(d.hasRedirectedFrom()).isTrue();
    }

    @Test
    public void getRedirectedFrom_Empty() {
        addNote("");
        assertThat(d.getRedirectedFrom()).isEqualTo("");
    }

    @Test
    public void getRedirectedFrom_Null() {
        addNote(null);
        d.addChangeNote("");
        assertThat(d.getRedirectedFrom()).isEqualTo("");
    }

    @Test
    public void getRedirectedFrom_Note() {
        addNote("Something");
        assertThat(d.getRedirectedFrom()).isEqualTo("");
    }

    @Test
    public void getRedirectedFrom_Forward() {
        addNote("Forward: url");
        assertThat(d.getRedirectedFrom()).isEqualTo("url");
    }

    @Test
    public void getRedirectedFrom_ForwardWithSpaces() {
        addNote("Forward: \n\r \n   url ");
        assertThat(d.getRedirectedFrom()).isEqualTo("url");
    }

    @Test
    public void getRedirectedFrom_MultipleNotes() {
        addNote("Forward: url");
        addNote("Something else");
        assertThat(d.getRedirectedFrom()).isEqualTo("url");
    }

    @Test
    public void getRedirectedFrom_MultipleNotesReverse() {
        addNote("Something something");
        addNote("Forward: url");
        assertThat(d.getRedirectedFrom()).isEqualTo("url");
    }

    private void addNote(String changeNote) {
        d.addChangeNote(changeNote);
    }
}
