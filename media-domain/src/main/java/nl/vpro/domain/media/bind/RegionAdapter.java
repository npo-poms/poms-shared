package nl.vpro.domain.media.bind;

import nl.vpro.domain.media.Region;
import nl.vpro.xml.bind.EnumAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
public class RegionAdapter extends EnumAdapter<Region>  {


    public RegionAdapter() {
        super(Region.class);
    }

    @Override
    protected  Region valueOf(String s) {
        return Region.valueOfOrNull(s);
    }

}
