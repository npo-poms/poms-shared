package nl.vpro.domain.npo.projectm;

import java.time.LocalDateTime;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.npo.projectm.metadata.v3_2.Aflevering;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.ServiceLocator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class MetaData_v3_2Test {

    MediaProvider provider = mock(MediaProvider.class);

    @BeforeEach
    public void init() {
        ClassificationServiceLocator.setInstance(new MediaClassificationService());
        ServiceLocator.setBroadcasterService("VPRO", "NCRV");
    }


    @Test
    public void testCreateAflevering() {
        Program program = getTestProgram(provider);

        Aflevering aflevering = MetaData_v3_2.createAflevering(program, provider);
        assertThat(aflevering.getPrid()).isEqualTo("mid_123");
        assertThat(aflevering.getPridexport()).isEqualTo("mid_123");
        assertThat(aflevering.getTitel()).isEqualTo("titel");
        assertThat(aflevering.getLexicoTitel()).isEqualTo("lexico");
        assertThat(aflevering.getIcon()).isNull();
        assertThat(aflevering.getInhk()).isEqualTo("kort");
        assertThat(aflevering.getKykw()).isEqualTo("3g");
        assertThat(aflevering.getOrti()).isEqualTo("origineel");
        assertThat(aflevering.getAfltitel()).isEqualTo("sub");

        assertThat(aflevering.getMail()).isEqualTo("bla@foo.bar");
        assertThat(aflevering.getWebs()).isEqualTo("http://www.vpro.nl");
        assertThat(aflevering.getTwitteraccount()).isEqualTo("@account");
        assertThat(aflevering.getTwitterhashtag()).isEqualTo("#hash");

        assertThat(aflevering.getGenre()).isEqualTo("Jeugd");
        assertThat(aflevering.getSubgenre()).isEqualTo("Documentaire");
        assertThat(aflevering.getOmroepen().getOmroep().get(0)).isEqualTo("VPRO");
        assertThat(aflevering.getOmroepen().getOmroep().get(1)).isEqualTo("NCRV");

        assertThat(aflevering.getPersonen().getPersoon().get(0).getNaam()).isEqualTo("Pietje Puk");
        assertThat(aflevering.getPersonen().getPersoon().get(0).getRol().get(0)).isEqualTo("Componist");

        assertThat(aflevering.getPersonen().getPersoon().get(1).getNaam()).isEqualTo("Jan Jansen");
        assertThat(aflevering.getPersonen().getPersoon().get(1).getRol().get(0)).isEqualTo("Regisseur");

        assertThat(aflevering.getTimestamp().toXMLFormat()).isEqualTo("2011-03-23T14:32:05.000+01:00");

        assertThat(aflevering.getSerie().getSrid()).isEqualTo("SEASON_MID");
        assertThat(aflevering.getSerie().getTitel()).isEqualTo("season title");
        // TODO testing not yet complete

        assertThat(aflevering.getParentserie().getPsrid()).isEqualTo("SERIES_MID");
        assertThat(aflevering.getParentserie().getTitel()).isEqualTo("series title");


    }

    // MSE-3088
    @Test
    public void testGetGenre() {
        Program program = getTestProgram(provider);
        SortedSet<Genre> genre = new TreeSet<>();
        genre.add(new Genre(ClassificationServiceLocator.getInstance().getTerm("3.0.1.7")));
        program.setGenres(genre);
        Aflevering aflevering = MetaData_v3_2.createAflevering(program, provider);
        assertThat(aflevering.getGenre()).isEqualTo("Informatief");
        assertThat(aflevering.getSubgenre()).isNull();


    }

    protected Program getTestProgram(MediaProvider provider) {

        Group series = MediaBuilder.group().mid("SERIES_MID").type(GroupType.SERIES).titles(new Title("series title", OwnerType.BROADCASTER, TextualType.MAIN)).build();
        when(provider.findByMid("SERIES_MID")).thenReturn(series);


        MemberRef seriesRef = new MemberRef("SERIES_MID", 5);
        seriesRef.setType(series.getType().getMediaType());

        Group season = MediaBuilder.group()
            .mid("SEASON_MID")
            .type(GroupType.SEASON)
            .titles(
                new Title("season title", OwnerType.BROADCASTER, TextualType.MAIN))
            .memberOf(seriesRef)
            .build();
        when(provider.findByMid("SEASON_MID")).thenReturn(season);


        MemberRef episodeRef = new MemberRef("SEASON_MID", 5);
        episodeRef.setType(season.getType().getMediaType());


        return MediaBuilder.program()
            .mid("mid_123")
            .type(ProgramType.BROADCAST)
            .titles(
                new Title("titel", OwnerType.BROADCASTER, TextualType.MAIN),
                new Title("lexico", OwnerType.BROADCASTER, TextualType.LEXICO),
                new Title("sub", OwnerType.BROADCASTER, TextualType.SUB),
                new Title("origineel", OwnerType.BROADCASTER, TextualType.ORIGINAL)
            )
            .descriptions(
                new Description("kort", OwnerType.BROADCASTER, TextualType.SHORT)
            )
            .ageRating(AgeRating._12).contentRatings(ContentRating.GEWELD)
            .emails("bla@foo.bar")
            .websites(new Website("http://www.vpro.nl"))
            .socialRefs("@account", "#hash")
            .genres(new Genre("3.0.1.1.8"))
            .broadcasters(new Broadcaster("VPRO"), new Broadcaster("NCRV"))
            .persons(new Person("Pietje", "Puk", RoleType.COMPOSER), new Person("Jan", "Jansen", RoleType.DIRECTOR))
            .lastModified(LocalDateTime.of(2011, 3, 23, 14, 32, 5))
            .episodeOf(episodeRef)
            .build();


    }
}
