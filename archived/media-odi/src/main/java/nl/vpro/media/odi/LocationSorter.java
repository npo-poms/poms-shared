package nl.vpro.media.odi;

import java.util.Comparator;

import nl.vpro.media.odi.util.LocationResult;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public interface LocationSorter {


    Comparator<LocationResult> getComparator(String... pubOptions);

}
