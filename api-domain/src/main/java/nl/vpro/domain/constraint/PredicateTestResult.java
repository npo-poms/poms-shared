package nl.vpro.domain.constraint;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;

import javax.el.*;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.*;

import nl.vpro.i18n.Locales;
import nl.vpro.i18n.LocalizedString;

/**
 * The result of a {@link DisplayablePredicate}.
 *
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Slf4j
@XmlAccessorType(XmlAccessType.NONE)
@JsonPropertyOrder({"reason", "applies", "description"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,  property = "objectType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = PredicateTestResult.class),
    @JsonSubTypes.Type(value = AndPredicateTestResult.class),
    @JsonSubTypes.Type(value = NotPredicateTestResult.class),
    @JsonSubTypes.Type(value = OrPredicateTestResult.class)
})
public class PredicateTestResult<T> {

    public static final ExpressionFactory FACTORY = ExpressionFactory.newInstance();


    private DisplayablePredicate<T> predicate;
    private List<String> bundleKey;


    @XmlAttribute
    private boolean applies;
    @XmlAttribute
    private String reason;

    @XmlElement(name = "description")
    private LocalizedString xmlDescription;

    private T value;

    PredicateTestResult(DisplayablePredicate<T> constraint, T value, boolean applies, List<String> bundleKey) {
        this.predicate = constraint;
        this.value = value;
        this.applies = applies;
        this.reason = constraint.getReason();
        this.bundleKey = bundleKey;
    }

    PredicateTestResult() {

    }

    public boolean applies() {
        return applies;
    }

    public String getReason() {
        return reason;
    }


    public String getDescription(Locale locale) {
        ELContext ctx = DisplayablePredicates.createELContext(FACTORY);

        if (predicate != null) {
            if (ctx != null){
                predicate.setELContext(ctx, value, locale, this);
                String description = DisplayablePredicates.getDescriptionTemplate(predicate, locale, bundleKey, applies);
                ValueExpression ve = FACTORY.createValueExpression(ctx, description, String.class);
                return ve.getValue(ctx).toString();
            } else {
                // shouldn't happen
                return DisplayablePredicates.getDescriptionTemplate(predicate, locale, bundleKey, applies).replace("${value}", String.valueOf(value));
            }
        } else {
            return null;
        }
    }

    @JsonProperty
    public LocalizedString getDescription() {
        if (xmlDescription == null) {
            if (predicate == null) {
                return null;
            }
            Locale locale = Locales.getDefault();

            xmlDescription = LocalizedString.of(getDescription(locale), locale);
        }
        return xmlDescription;
    }


    @Override
    public String toString() {
        if (predicate != null) {
            return predicate.getDefaultBundleKey() + "[" + value + "]";
        } else {
            return  reason + "(" + applies + ")";
        }
    }

    void beforeMarshal(Marshaller unmarshaller) {
        getDescription();
    }

}
