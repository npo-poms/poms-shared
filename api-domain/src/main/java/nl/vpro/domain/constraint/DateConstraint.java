package nl.vpro.domain.constraint;

import java.time.Instant;
import java.util.Date;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import com.joestelmach.natty.Parser;

import nl.vpro.util.DateUtils;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
@XmlTransient
public abstract class DateConstraint<T> implements FieldConstraint<T> {

    private static final Parser PARSER = new Parser();

    @XmlAttribute
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
        switch(operator) {
            case LT:
                return date.isBefore(getDateAsInstant());
            case GT:
                return date.isAfter(getDateAsInstant());
            case LTE:
                return ! date.isBefore(getDateAsInstant());
            case GTE:
                return ! date.isAfter(getDateAsInstant());

        }
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(operator) + " " + date;
    }


}
