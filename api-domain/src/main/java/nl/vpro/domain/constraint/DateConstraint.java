package nl.vpro.domain.constraint;

import java.time.Instant;
import java.util.Date;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.meeuw.xml.bind.annotation.XmlDocumentation;
import org.natty.Parser;

import nl.vpro.util.DateUtils;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
@XmlTransient
public abstract class DateConstraint<T> implements FieldConstraint<T> {

    private static final Parser PARSER = new Parser();

    @XmlAttribute
    @XmlDocumentation("A timestamp spec. This is parsed by the natty parser. Try out with http://natty.joestelmach.com/try.jsp")
    private String date;

    @XmlAttribute
    private Operator operator;

    @Override
    public boolean test(@Nullable T input) {
        return false;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        getDateAsDate(); // throws errors if not parsing...
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Date getDateAsDate() {
        return PARSER.parse(date).get(0).getDates().get(0);
    }

    public Instant getDateAsInstant() {
        return DateUtils.toInstant(PARSER.parse(date).get(0).getDates().get(0));
    }


    protected boolean applyDate(Instant date) {
        if (date == null) return false;
        return switch (operator) {
            case LT -> date.isBefore(getDateAsInstant());
            case GT -> date.isAfter(getDateAsInstant());
            case LTE -> !date.isAfter(getDateAsInstant());
            case GTE -> !date.isBefore(getDateAsInstant());
            default -> false;
        };
    }

    @Override
    public String toString() {
        return operator + " " + date;
    }


}
