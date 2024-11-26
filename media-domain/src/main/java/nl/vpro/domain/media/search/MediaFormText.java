package nl.vpro.domain.media.search;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.media.bind.MediaFormTextJson;

/**
 * @since 8.4
 */
@Setter
@Getter
@JsonSerialize(using = MediaFormTextJson.Serializer.class)
@JsonDeserialize(using = MediaFormTextJson.Deserializer.class)
@XmlAccessorType(XmlAccessType.NONE)
public class MediaFormText {

    @XmlAttribute
    BooleanOperator booleanOperator = null;

    @XmlAttribute
    Boolean exactMatching = null;

    @XmlAttribute
    Boolean implicitWildcard = null;

    @XmlValue
    private String value;

    private String parsedText;

    public boolean needsAttributes() {
        return booleanOperator != null || implicitWildcard != null || exactMatching != null;
    }


    public MediaFormText() {

    }

    public MediaFormText(String value) {
        this.value = value;
    }

    public enum BooleanOperator {
        AND,
        OR
    }

    public void setValue(String value) {
        this.value = value;
        this.parsedText = null;
    }

    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }



    public boolean isQuoted() {
        if (! isEmpty() && value.length() > 2) {
            var first = value.charAt(0);
            if (first == '\'' || first == '"') {
                return value.charAt(value.length() - 1) == first;
            }
        }
        return false;
    }

    public String getUnQuotedValue() {
        if (isQuoted()) {
            return value.substring(1, value.length() - 1);
        } else {
            return value;
        }
    }
    public String getParsedText() {
        if (parsedText == null) {
            if (value == null) {
                return null;
            }
            String queryStringText = getUnQuotedValue().toLowerCase().replaceAll("[^\\p{IsAlphabetic}&!+\\-\\d\\s]", "");
            if (queryStringText.length() > 1) {
                parsedText =  parseANDandNOT(queryStringText);
            } else {
                parsedText =  queryStringText;
            }
        }
        return parsedText;
    }
    public Optional<String> getWildcard() {
        if (!isQuoted()) {
            Boolean implicitWildcard = getImplicitWildcard();
            if ((implicitWildcard != null && implicitWildcard) && !value.endsWith(" ")) {
                // the last word will be implicitly converted to a wildcard. The assumption being that the user is still typing
                String parsed = getParsedText();
                List<String> split = Arrays.asList(parsed.trim().split("\\s+"));
                var lastWord = split.getLast();
                return Optional.of(lastWord + "*");
            }
            if (value.endsWith("*")) {
                String parsed = getParsedText();
                List<String> split = Arrays.asList(parsed.trim().split("\\s+"));
                var lastWord = split.getLast();
                return Optional.of(lastWord + "*"); // * is removed by getParsedText()
            }
        }
        return Optional.empty();
    }
    static String parseANDandNOT(String string) {
        return parseAND(parseNOT(string));
    }

    private static final Pattern NOT = Pattern.compile("(\\s+|^)(?:not\\s+|!\\s*)(\\S+?)");
    static String parseNOT(String string) {
        return NOT.matcher(string).replaceAll( "$1-$2");
    }

    private static final Pattern AND = Pattern.compile("(\\s+and\\s+|\\s*&\\s*)");

    static String parseAND(String string) {
        String[] splitComplete =  string.split("\\s+");
        if (splitComplete.length > 1 && splitComplete[0].equals("and")) {
            return Arrays.stream(splitComplete)
                .filter(s -> ! "and".equals(s))
                .map(s->prefixWith(s, '+'))
                .collect(Collectors.joining(" "));
        }


        String[] split = AND.splitAsStream(string).toArray(String[]::new);
        if (split.length > 1) {
            return  Arrays.stream(split)
                .map(s->prefixWith(s, '+'))
                .collect(Collectors.joining(" "));
        }
        return string;
    }

    private static String prefixWith(String s, char c) {
        if (Character.isLetterOrDigit(s.charAt(0))) {
            return c + s;
        } else {
            return s;
        }
    }

    @Override
    public String toString() {
        return value + " (" + getParsedText() + ")";
    }

}
