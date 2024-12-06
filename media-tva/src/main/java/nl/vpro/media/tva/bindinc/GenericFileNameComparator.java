package nl.vpro.media.tva.bindinc;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import org.apache.camel.component.file.GenericFile;

/**
 *
 * @author Michiel Meeuwissen
 * @see FileNameComparator
 */
public class GenericFileNameComparator implements Comparator<GenericFile<?>>, Serializable {
    @Serial
    private static final long serialVersionUID = -4923818665598016125L;
    private static final FileNameComparator fn = new FileNameComparator();
    @Override
    public int compare(GenericFile<?> o1, GenericFile<?> o2) {
        return fn.compare(o1.getFileName(), o2.getFileName());
    }
}
