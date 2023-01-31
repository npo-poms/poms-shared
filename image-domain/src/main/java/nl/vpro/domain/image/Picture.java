package nl.vpro.domain.image;

import java.util.Map;

public interface Picture {

    Map<String, String> getSources();

    String getImageSrc();

    String getAlternative();

    String getStyle();

}
