package nl.vpro.domain.user;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import nl.vpro.i18n.Displayable;

import static org.assertj.core.api.Assertions.assertThat;

class BroadcasterTest {

    @Test
    public void existingFirst() {
        Broadcaster b1 = Broadcaster.builder().displayName("AVTR").build();
        Broadcaster b2 = Broadcaster.builder().displayName("VPRO").build();
        Broadcaster ob1 = Broadcaster.builder().displayName("AVRO").stop(LocalDate.of(2012, 1, 1)).build();
        Broadcaster ob2 = Broadcaster.builder().displayName("KRO").stop(LocalDate.of(2012, 1, 1)).build();
        Comparator<Broadcaster> comparor = Broadcaster.existingFirst(LocalDate.of(2022, 2, 11));
        List<Broadcaster> list = new ArrayList<>();
        list.add(b1);
        list.add(b2);
        list.add(null);
        list.add(ob2);
        list.add(ob1);
        list.sort(comparor);

        assertThat(list.get(0).getDisplayName()).isEqualTo("AVTR");
        assertThat(list.stream()
            .map(Displayable::of)
            .map(Displayable::getDisplayName)
            .collect(Collectors.toList()).toString())
            .isEqualTo("[AVTR, VPRO, AVRO, KRO, null]");

    }

}
