package nl.vpro.domain.page;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.classification.Term;
import nl.vpro.domain.classification.bind.TermWrapperJsonString;

/**
 * Extension just to move it to correct namespace
 * @author Michiel Meeuwissen
 * @since 3.3.8
 */
@XmlType(name = "pageTermType")
@XmlAccessorType(XmlAccessType.NONE)
@JsonSerialize(using = TermWrapperJsonString.Serializer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = TermWrapper.Deserializer.class)
public class TermWrapper extends nl.vpro.domain.classification.bind.AbstractTermWrapper {

    public TermWrapper() {

    }
    public TermWrapper(String name) {
        super(name);
    }
    public TermWrapper(Term term) {
        super(term);
    }

    public static class Deserializer extends TermWrapperJsonString.Deserializer<TermWrapper> {

        @Override
        protected TermWrapper getTermWrapper() {
            return new TermWrapper();
        }
    }
}
