package nl.vpro.domain.media;

import java.util.*;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @since 8.10
 */
public record Generation(Set<RecursiveParentChildRelation> members, int level) {

    public Generation(Set<RecursiveParentChildRelation> members, int level) {
        this.members = sorted(members);
        this.level = level;
    }

    public Generation up() {
        Set<RecursiveParentChildRelation> parents = new HashSet<>();
        for (RecursiveParentChildRelation member : members) {
            Optional.ofNullable(member.getSegmentOf()).ifPresent(parents::add);
            Optional.ofNullable(member.getEpisodeOf()).ifPresent(parents::addAll);
            Optional.ofNullable(member.getMemberOf()).ifPresent(parents::addAll);
        }
        return new Generation(parents, level + 1);
    }

    public boolean isNotEmpty() {
        return ! members.isEmpty();
    }

    @Override
    @NonNull
    public String toString() {
        return level + " " + members.stream().map(m -> m.getType() + ":" + m.getParentMid()).collect(Collectors.joining(","));
    }

    private static SortedSet<RecursiveParentChildRelation> sorted(Set<RecursiveParentChildRelation> set) {

        // sorted
        // Reversed because then 'SEASON' comes for 'SERIES', which I guess is the use case.
        SortedSet<RecursiveParentChildRelation> ss = new TreeSet<>(
            Comparator.comparing(ParentChildRelation::getType)
                .reversed()
                .thenComparing(ParentChildRelation::getParentMid)
        );
        ss.addAll(set);
        return ss;

    }
}
