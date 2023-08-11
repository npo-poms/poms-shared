package nl.vpro.domain;

import nl.vpro.domain.user.Editor;

/**
 * The basic accountability fields like 'last modified by', 'last modified instant' and 'created by'.
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface Accountable extends Changeable {

    Editor getCreatedBy();
    void setCreatedBy(Editor createdBy);
    Editor getLastModifiedBy();
    void setLastModifiedBy(Editor lastModifiedBy);

}
