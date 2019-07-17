package nl.vpro.domain.classification;

import java.util.Arrays;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class TermId implements Comparable<TermId> {

    final int[] parts;

    public static TermId of(String id) {
        return new TermId(id);
    }

    public TermId(String id) {
        String[] stringParts = id.split("\\.");
        parts = new int[stringParts.length];
        for (int i = 0; i < stringParts.length; i++) {
            parts[i] = Integer.parseInt(stringParts[i]);
        }
    }
    public TermId(int[] parts) {
        this.parts = parts;
    }

    @Override
    public int compareTo(@NonNull TermId o) {
        if (o == null) return 1;
        for (int i = 0; i < Math.min(parts.length, o.parts.length); i++) {
            int diff = parts[i] - o.parts[i];
            if (diff != 0) return diff;
        }
        return parts.length - o.parts.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TermId termId = (TermId) o;

        return Arrays.equals(parts, termId.parts);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(parts);
    }

    @Override
    public String toString() {
        StringBuilder build = new StringBuilder();
        for (int part : parts) {
            if (build.length() > 0) build.append('.');
            build.append(part);
        }

        return build.toString();
    }


    public int[] getParts() {
        return parts;
    }

    public TermId getParentId() {
        if (this.parts.length > 1) {
            int[] clone = new int[this.parts.length - 1];
            System.arraycopy(parts, 0, clone, 0, clone.length);
            return new TermId(clone);
        } else {
            return null;
        }
    }

    public TermId next() {
        int[] clone = this.parts.clone();
        clone[clone.length - 1]++;
        return new TermId(clone);
    }

    public TermId first() {
        int[] clone = new int[this.parts.length + 1];
        System.arraycopy(parts, 0, clone, 0, parts.length);
        clone[parts.length] = 0;
        return new TermId(clone);
    }
}
