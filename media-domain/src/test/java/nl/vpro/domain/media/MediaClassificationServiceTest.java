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
class MediaClassificationServiceTest {

    @BeforeAll
    public static void init() {
        ClassificationServiceLocator.setInstance(new MediaClassificationService());
    }


    @Test
    void getTermByEpgCodeOnRawCode() {
        assertThat(MediaClassificationService.getTermByEpgCode("0311")).isNotNull();
    }

    @Test
    void getTermByEpgCodeWhenNameSpaced() {
        assertThat(MediaClassificationService.getTermByEpgCode("urn:tva:metadata:cs:2004:0725").getName()).isEqualTo("Natuur");
    }

    @Test
    void getTermByMisCode() {
        assertThat(MediaClassificationService.getTermsByMisGenreType("MUSIC")).hasSize(1);
    }

    @Test
    //Reproduces MSE-2472
    void legacyMisMatching() {
        for (EpgGenreType epg : EpgGenreType.values()) {
            List<String> example = new ArrayList<>();
            for (MisGenreType mis : epg.getLegacyGenre()) {
                example.add(mis.getDisplayName());
            }
            MediaClassificationService.getTermsByMisGenreType(example.toArray(new String[0]));
        }
    }

    @Test
    //MSE-2488
    void legacySpelQuiz() {
        assertThat(MediaClassificationService.getLegacyMisGenres("3.0.1.6.19")).containsExactly("ENTERTAINMENT");
    }

    @Test
    void legacyYouthMovies() {
        assertThat(MediaClassificationService.getLegacyMisGenres("3.0.1.1.2")).containsExactly("YOUTH", "MOVIE");
    }

    @Test
    void getTermByMisCodeOmitParent() {
        assertThat(MediaClassificationService.getTermsByMisGenreType("CARTOON", "MOVIE")).hasSize(1);
        assertThat(MediaClassificationService.getTermsByMisGenreType("CARTOON", "MOVIE").get(0).getName()).isEqualTo("Animatie");
        assertThat(MediaClassificationService.getTermsByMisGenreType("CARTOON", "MOVIE").get(0).getParent().getName()).isEqualTo("Film");
    }

    @Test
    void getTermByMisCodes() {
        assertThat(MediaClassificationService.getTermsByMisGenreType("CARTOON", "YOUTH")).hasSize(1);
        assertThat(MediaClassificationService.getTermsByMisGenreType("CARTOON", "YOUTH").get(0).getName()).isEqualTo("Animatie");
        assertThat(MediaClassificationService.getTermsByMisGenreType("CARTOON", "YOUTH").get(0).getParent().getName()).isEqualTo("Jeugd");
    }



}
