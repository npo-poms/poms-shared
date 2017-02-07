package nl.vpro.domain.media.bind;

import nl.vpro.domain.media.Platform;
import nl.vpro.xml.bind.EnumAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public class PlatformAdapter extends EnumAdapter<Platform>  {
    public PlatformAdapter() {
        super(Platform.class);
    }

}
