package nl.vpro.domain.media.bind;

import nl.vpro.domain.media.Region;
import nl.vpro.xml.bind.EnumAdapter;

/**
 * Nice idea, but we don't use it for now. Since it ****-up XSD generation.
 * @author Michiel Meeuwissen
 * @since 5.8
 * @Beta
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
