package nl.vpro.w3.rdf;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DescriptionTest {

    private Description d;

    @BeforeEach
    public void init() {
        d = new Description();
    }


    @Test
    void getRedirectedFrom_Empty() {
        addNote("");
        assertThat(d.getRedirectedFrom()).isNotPresent();
    }

    @Test
    void getRedirectedFrom_Null() {
        addNote(null);
        d.addChangeNote("");
        assertThat(d.getRedirectedFrom()).isNotPresent();
    }

    @Test
    void getRedirectedFrom_Note() {
        addNote("Something");
        assertThat(d.getRedirectedFrom()).isEmpty();
    }

    @Test
    void getRedirectedFrom_Forward() {
        addNote("Forward: url");
        assertThat(d.getRedirectedFrom()).isPresent().hasValue(URI.create("url"));
    }

    @Test
    void getRedirectedFrom_ForwardWithSpaces() {
        addNote("Forward: \n\r \n   url ");
        assertThat(d.getRedirectedFrom()).hasValue(URI.create("url"));
    }

    @Test
    void getRedirectedFrom_MultipleNotes() {
        addNote("Forward: url");
        addNote("Something else");
        assertThat(d.getRedirectedFrom()).hasValue(URI.create("url"));
    }

    @Test
    void getRedirectedFrom_MultipleNotesReverse() {
        addNote("Something something");
        addNote("Forward: url");
        assertThat(d.getRedirectedFrom()).hasValue(URI.create("url"));
    }

    private void addNote(String changeNote) {
        d.addChangeNote(changeNote);
    }
}
