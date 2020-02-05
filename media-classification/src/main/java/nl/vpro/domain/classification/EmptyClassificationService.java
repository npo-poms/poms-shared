package nl.vpro.domain.classification;

import java.util.Collections;
import java.util.List;

import org.xml.sax.InputSource;

/**
 * @author Michiel Meeuwissen
 * @since 4.2
 */
public class EmptyClassificationService extends AbstractClassificationServiceImpl {

    public static final ClassificationService INSTANCE = new EmptyClassificationService();

    @Override
    protected List<InputSource> getSources(boolean init) {
        return Collections.emptyList();

    }
}
