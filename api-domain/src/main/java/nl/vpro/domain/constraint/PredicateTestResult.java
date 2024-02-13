package nl.vpro.domain.constraint;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;

import jakarta.el.*;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.*;

import nl.vpro.i18n.Locales;

/**
 * The result of a {@link DisplayablePredicate}.
 *
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Slf4j
@XmlAccessorType(XmlAccessType.NONE)
@JsonPropertyOrder({"reason", "applies", "description"})
// https://github.com/swagger-api/swagger-core/issues/2289
// swagger sucks, we should consider migrating away from it. The code is very brittle, doesn't produce the right yaml, and pull requests are ignored.
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,  property = "objectType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = SimplePredicateTestResult.class),
    @JsonSubTypes.Type(value = AndPredicateTestResult.class),
    @JsonSubTypes.Type(value = NotPredicateTestResult.class),
    @JsonSubTypes.Type(value = OrPredicateTestResult.class)
})
@XmlTransient
@XmlSeeAlso({AndPredicateTestResult.class, OrPredicateTestResult.class, NotPredicateTestResult.class, SimplePredicateTestResult.class})
public abstract class PredicateTestResult implements BooleanSupplier {

    public static final ExpressionFactory FACTORY = ExpressionFactory.newInstance();

    private DisplayablePredicate<?> predicate;
    private List<String> bundleKey;

    @XmlAttribute
    private boolean applies;
    @XmlAttribute
    private String reason;

    @XmlElement(name = "description")
    private LocalizedString xmlDescription;

    private Object value;

    PredicateTestResult(DisplayablePredicate<?> constraint, Object value, boolean applies, List<String> bundleKey) {
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

    @Override
    public boolean getAsBoolean() {
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
