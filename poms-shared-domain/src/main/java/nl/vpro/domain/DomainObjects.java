package nl.vpro.domain;

import java.io.File;
import java.nio.file.Path;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.function.Consumer;

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

    public static void appendCanonicalFilePath(long i, @NonNull StringBuilder builder) {
        appendCanonicalFilePath(String.valueOf(i), builder);
    }

    public static void appendCanonicalFilePath(@NonNull final String id, @NonNull StringBuilder builder) {
        appendCanonicalFilePath(id, (p) -> builder.append(File.separator).append(p));
    }

    public static Path appendCanonicalFilePath(long id, @NonNull Path path) {
        return appendCanonicalFilePath(String.valueOf(id), path);
    }

    public static Path appendCanonicalFilePath(@NonNull final String id, @NonNull Path path) {
        final Path[] p = new Path[]{path};
        appendCanonicalFilePath(id, s -> p[0] = p[0].resolve(s));
        return p[0];
    }

    private  static void appendCanonicalFilePath(@NonNull final String id, Consumer<String> appender) {

        final CharacterIterator it = new StringCharacterIterator(id);
        final StringBuilder builder = new StringBuilder();
        for(char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
            builder.append(c);
            // Second digit
            c = it.next();
            if(c != CharacterIterator.DONE) {
                builder.append(c);
            }
            appender.accept(builder.toString());
            builder.setLength(0);
        }
    }

}
