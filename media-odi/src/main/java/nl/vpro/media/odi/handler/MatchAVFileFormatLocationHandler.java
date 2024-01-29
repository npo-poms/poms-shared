package nl.vpro.media.odi.handler;

import lombok.ToString;

import jakarta.servlet.http.HttpServletRequest;

import nl.vpro.domain.media.AVFileFormat;
import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.Platform;
import nl.vpro.media.odi.LocationProducer;
import nl.vpro.media.odi.util.LocationResult;

/**
 * @author Michiel Meeuwissen
 * @since 4.9
 */
@ToString
public class MatchAVFileFormatLocationHandler implements LocationProducer {

    @Override
    public int score(Location location, String... pubOptions) {
        if (location.getPlatform() != null && location.getPlatform() != Platform.INTERNETVOD) {
            return 0;
        }
        for (int i = 0 ; i < pubOptions.length; i++) {
            try {
                AVFileFormat format = AVFileFormat.valueOf(pubOptions[i].toUpperCase());
                if (location.getAvFileFormat() == format) {
                    return 2 + pubOptions.length - i;
                }
            } catch (IllegalArgumentException iae) {

            }

        }
        return 0;

    }

    @Override
    public LocationResult produce(Location location, HttpServletRequest request, String... pubOptions) {
        return LocationResult.of(location);

    }
}
