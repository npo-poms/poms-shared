package nl.vpro.domain;

import java.io.File;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * @author Michiel Meeuwissen
 * @since 5.7
 */
public class DomainObjects {



    public static String getCanonicalFilePath(DomainObject domain) {
        String id = String.valueOf(domain.getId());
        StringBuilder path = new StringBuilder(id.length() * 2);
        appendCanonicalFilePath(domain, path);
        return path.toString();
    }


    public static void appendCanonicalFilePath(DomainObject domain, StringBuilder builder) {
        appendCanonicalFilePath(domain.getId(), builder);
    }

    public static void appendCanonicalFilePath(Long i, StringBuilder builder) {
        if (i == null) {
            throw new IllegalArgumentException();
        }
        appendCanonicalFilePath(String.valueOf(i), builder);
    }
     public static void appendCanonicalFilePath(String i, StringBuilder builder) {
        if (i == null) {
            throw new IllegalArgumentException();
        }
        String id = String.valueOf(i);

        CharacterIterator it = new StringCharacterIterator(id);
        for(char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
            builder.append(c);
            // Second digit
            c = it.next();
            if(c != CharacterIterator.DONE) {
                builder.append(c);
            }

            builder.append(File.separator);
        }
    }

}
