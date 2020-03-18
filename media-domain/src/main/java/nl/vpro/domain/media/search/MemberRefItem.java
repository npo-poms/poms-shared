package nl.vpro.domain.media.search;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import nl.vpro.domain.media.MemberRef;

/**
 * @author Michiel Meeuwissen
 * @since 3.5
 */

@Getter
@Setter
public class MemberRefItem {

    private Long id;

    private Integer number;

    private MediaListItem member;

    private MediaListItem owner;

    private Instant added;

    private Boolean highlighted;

    public static MemberRefItem create(MemberRef ref, MediaListItem member) {
        MemberRefItem item = new MemberRefItem();
        item.setId(ref.getId());
        item.setNumber(ref.getNumber());
        item.setAdded(ref.getAdded());
        item.setHighlighted(ref.isHighlighted());
        item.setMember(member);
        item.setOwner(new MediaListItem(ref.getGroup()));
        return item;
    }

    public static MemberRefItem create(MemberRef ref) {
        return create(ref, new MediaListItem(ref.getMember()));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + owner + "-" + number + "->" + member;
    }
}
