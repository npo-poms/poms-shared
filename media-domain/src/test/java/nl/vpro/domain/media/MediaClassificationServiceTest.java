package nl.vpro.domain.media;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.classification.ClassificationServiceLocator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class MediaClassificationServiceTest {

    @BeforeAll
    public static void init() {
        ClassificationServiceLocator.setInstance(new MediaClassificationService());
    }


    @Test
    public void testGetTermByEpgCodeOnRawCode() {
        assertThat(MediaClassificationService.getTermByEpgCode("0311")).isNotNull();
    }

    @Test
    public void testGetTermByEpgCodeWhenNameSpaced() {
        assertThat(MediaClassificationService.getTermByEpgCode("urn:tva:metadata:cs:2004:0725").getName()).isEqualTo("Natuur");
    }

    @Test
    public void testGetTermByMisCode() {
        assertThat(MediaClassificationService.getTermsByMisGenreType("MUSIC")).hasSize(1);
    }

    @Test
    //Reproduces MSE-2472
    public void testLegacyMisMatching() {
        for (EpgGenreType epg : EpgGenreType.values()) {
            List<String> example = new ArrayList<>();
            for (MisGenreType mis : epg.getLegacyGenre()) {
                example.add(mis.getDisplayName());
            }
            MediaClassificationService.getTermsByMisGenreType(example.toArray(new String[example.size()]));
        }
    }

    @Test
    //MSE-2488
    public void legacySpelQuiz() {
        assertThat(MediaClassificationService.getLegacyMisGenres("3.0.1.6.19")).containsExactly("ENTERTAINMENT");
    }

    @Test
    public void legacyYouthMovies() {
        assertThat(MediaClassificationService.getLegacyMisGenres("3.0.1.1.2")).containsExactly("YOUTH", "MOVIE");
    }

    @Test
    public void testGetTermByMisCodeOmitParent() {
        assertThat(MediaClassificationService.getTermsByMisGenreType("CARTOON", "MOVIE")).hasSize(1);
        assertThat(MediaClassificationService.getTermsByMisGenreType("CARTOON", "MOVIE").get(0).getName()).isEqualTo("Animatie");
        assertThat(MediaClassificationService.getTermsByMisGenreType("CARTOON", "MOVIE").get(0).getParent().getName()).isEqualTo("Film");
    }

    @Test
    public void testGetTermByMisCodes() {
        assertThat(MediaClassificationService.getTermsByMisGenreType("CARTOON", "YOUTH")).hasSize(1);
        assertThat(MediaClassificationService.getTermsByMisGenreType("CARTOON", "YOUTH").get(0).getName()).isEqualTo("Animatie");
        assertThat(MediaClassificationService.getTermsByMisGenreType("CARTOON", "YOUTH").get(0).getParent().getName()).isEqualTo("Jeugd");
    }



}
