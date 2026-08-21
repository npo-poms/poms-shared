package nl.vpro.media.odi.handler;

import lombok.ToString;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import nl.vpro.media.odi.LocationSorter;
import nl.vpro.media.odi.util.LocationResult;

/**
 * @author Michiel Meeuwissen
 * @since 4.9
 */
@ToString
public class AVFileFormatLocationSorter implements LocationSorter {

    @Override
    public Comparator<LocationResult> getComparator(String... pubOptions) {
        List<String> list = Arrays.asList(pubOptions);
        return (o1, o2) -> {
            int indexof1 = list.indexOf(o1.getAvFileFormat().name().toLowerCase());
            int indexof2 = list.indexOf(o2.getAvFileFormat().name().toLowerCase());

            return Comparator.<Integer>nullsLast(Comparator.naturalOrder()).compare(indexof1 == -1 ? null : indexof1, indexof2 == -1 ? null : indexof2);
        };


    }
}
