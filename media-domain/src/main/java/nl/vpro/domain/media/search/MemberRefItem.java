package nl.vpro.domain.media.search;

import java.time.Instant;

import nl.vpro.domain.media.MemberRef;

/**
 * @author Michiel Meeuwissen
 * @since 3.5
 */
public class MemberRefItem {

    private Long id;

    private Integer number;

    private MediaListItem member;

    private MediaListItem owner;

    private Instant added;

    private Boolean highlighted;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Instant getAdded() {
        return added;
    }

    public void setAdded(Instant added) {
        this.added = added;
    }

    public Boolean getHighlighted() {
        return highlighted;
    }

    public void setHighlighted(Boolean highlighted) {
        this.highlighted = highlighted;
    }

    public void setMember(MediaListItem member) {
        this.member = member;
    }

    public MediaListItem getMember() {
        return member;
    }

    public MediaListItem getOwner() {
        return owner;
    }

    public void setOwner(MediaListItem owner) {
        this.owner = owner;
    }

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
}
