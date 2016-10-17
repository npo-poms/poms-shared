/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;

import nl.vpro.domain.media.AVFileFormat;

public abstract class MediaUpdateTest {

    @Before
    public void setup()  {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
    }


    protected AVAttributesUpdate avAttributes() {
        return new AVAttributesUpdate(
            AVFileFormat.H264,
            1000,
            new AudioAttributesUpdate(2, "AAC"),
            new VideoAttributesUpdate(320, 180));
    }
}
