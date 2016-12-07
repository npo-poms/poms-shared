package nl.vpro.media.odi.handler;

import javax.servlet.http.HttpServletRequest;

import nl.vpro.domain.media.AVFileFormat;
import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.Platform;
import nl.vpro.media.odi.LocationHandler;
import nl.vpro.media.odi.util.LocationResult;

/**
 * @author Michiel Meeuwissen
 * @since 4.9
 */
public class MatchAVFileFormatLocationHandler implements LocationHandler {

    @Override
    public int score(Location location, String... pubOptions) {
        if (location.getPlatform() != null && location.getPlatform() != Platform.INTERNETVOD) {
            return 0;
        }
        for (String puboption : pubOptions) {
            try {
                AVFileFormat format = AVFileFormat.valueOf(puboption.toUpperCase());
                if (location.getAvFileFormat() == format) {
                    return 2;
                }
            } catch (IllegalArgumentException iae) {

            }

        }
        return 0;

    }

    @Override
    public LocationResult handle(Location location, HttpServletRequest request, String... pubOptions) {
        return LocationResult.of(location);

    }
}
