package nl.vpro.media.tva.bindinc;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * To sort the files grouped by channel/day.
 * @author Michiel Meeuwissen
 */
@Slf4j
public class FileNameComparator implements Comparator<String>, Serializable {

    //@Serial
    @Serial
    private static final long serialVersionUID = -3188480559809189586L;

    @Override
    public int compare(String o1, String o2) {
        Optional<Utils.BindincFile> bf1 = Utils.parseFileName(o1);
        Optional<Utils.BindincFile> bf2 = Utils.parseFileName(o2);
        if (bf1.isPresent() && bf2.isPresent()) {
            return bf1.get().compareTo(bf2.get());
        } else {
            if (bf1.isPresent()) {
                return -1;
            } else if (bf2.isPresent()) {
                return 1;
            } else {
                return Comparator.<String>nullsLast(Comparator.naturalOrder()).compare(o1, o2);
            }
        }
    }




}
