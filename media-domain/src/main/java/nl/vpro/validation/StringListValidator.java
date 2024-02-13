package nl.vpro.validation;

import java.util.Collection;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author Michiel Meeuwissen
 * @since 3.1.0
 */
public class StringListValidator implements ConstraintValidator<StringList, Collection<String>> {

    private int maxLength = Integer.MAX_VALUE;
    private int minLength = 0;
    private boolean notNull = true;
    private Pattern pattern = null;


    @Override
    public void initialize(StringList stringList) {
        maxLength = stringList.maxLength();
        minLength = stringList.minLenght();
        notNull = stringList.notNull();
        String p = stringList.pattern();
        pattern = ".*".equals(p) || p == null ? null : Pattern.compile(p);

    }

    @Override
    public boolean isValid(Collection<String> value, ConstraintValidatorContext context) {
        if (value == null) return true;
        int i = -1;
        StringBuilder message = new StringBuilder();
        for (String s : value) {
            i++;
            if (s == null) {
                if (notNull) {
                    appendError(message, i, "value is null");
                }
                continue;
            }
            if (s.length() > maxLength) {
                appendError(message, i, "value is longer than {maxLength}");
            }
            if (s.length() < minLength) {
                appendError(message, i, "value is smaller than {minLength}");
            }
            if (pattern != null && !pattern.matcher(s).matches()) {
                appendError(message, i, "value does not match {pattern}");
            }
        }
        if (!message.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message.toString()).addConstraintViolation();
            return false;
        }
        return true;
    }
    private void appendError(StringBuilder message, int i, String template) {
        if (!message.isEmpty()) {
            message.append("\n");
        }
        message.append('[').append(i).append("] ").append(template);
    }

}
