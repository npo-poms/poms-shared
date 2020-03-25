package nl.vpro.domain;

import java.io.File;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @author Michiel Meeuwissen
 * @since 5.7
 */
public class DomainObjects {

    private DomainObjects() {
    }

    public static String getCanonicalFilePath(@NonNull DomainObject domain) {
        String id = String.valueOf(domain.getId());
        StringBuilder path = new StringBuilder(id.length() * 2);
        appendCanonicalFilePath(domain, path);
        return path.toString();
    }


    public static void appendCanonicalFilePath(@NonNull DomainObject domain, @NonNull StringBuilder builder) {
        appendCanonicalFilePath(domain.getId(), builder);
    }

    public static void appendCanonicalFilePath(@NonNull Long i, @NonNull StringBuilder builder) {
        if (i == null) {
            throw new IllegalArgumentException();
        }
        appendCanonicalFilePath(String.valueOf(i), builder);
    }
     public static void appendCanonicalFilePath(@NonNull final String id, @NonNull StringBuilder builder) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

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
