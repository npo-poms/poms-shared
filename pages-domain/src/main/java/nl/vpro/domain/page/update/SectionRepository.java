package nl.vpro.domain.page.update;

import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface SectionRepository {

    List<String> namesForSectionPath(String path, String portalId);

}
