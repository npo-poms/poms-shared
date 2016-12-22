package nl.vpro.domain.api.secondscreen;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.Result;
import nl.vpro.domain.secondscreen.Screen;

/**
 * @author Michiel Meeuwissen
 * @since 4.1
 */
@XmlRootElement(name = "secondscreenResult")
@XmlType(name = "secondscreenResultType")
public class ScreenResult extends Result<Screen> {


    public ScreenResult() {
    }

    public ScreenResult(List<? extends Screen> screens, Long offset, Integer max, long listSizes) {
        super(screens, offset, max, listSizes);
    }

    public ScreenResult(Result<? extends Screen> screens) {
        super(screens);
    }
}
