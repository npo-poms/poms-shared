package nl.vpro.media.odi.handler;

import javax.servlet.http.HttpServletRequest;

import nl.vpro.domain.media.AVFileFormat;
import nl.vpro.domain.media.Location;
import nl.vpro.media.odi.LocationHandler;
import nl.vpro.media.odi.util.LocationResult;

/**
 * @author Michiel Meeuwissen
 * @since 4.9
 */
public class MatchAVFileFormatLocationHandler implements LocationHandler {

    @Override
    public boolean supports(Location location, String... pubOptions) {
        for (String puboption : pubOptions) {
            try {
                AVFileFormat format = AVFileFormat.valueOf(puboption.toUpperCase());
                if (location.getAvFileFormat() == format) {
                    return true;
                }
            } catch (IllegalArgumentException iae) {

            }

        }
        return false;

    }

    @Override
    public LocationResult handle(Location location, HttpServletRequest request, String... pubOptions) {
        return LocationResult.of(location);

    }
}
