package nl.vpro.domain;

import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface OwnedText extends Ownable, TypedText, Comparable<TypedText> {

    static OwnedText of(TypedText t, OwnerType ownerType) {
        return new BasicOwnedText(t.get(), ownerType, t.getType());
    }

    @Override
    default int compareTo(TypedText o) {
        if (o == null) {
            return -1;
        }
        TextualType type = getType();
        if (o instanceof OwnedText) {
            OwnerType owner = getOwner();
            OwnedText ownedText = (OwnedText) o;
            if (type != null && type.equals(o.getType()) && owner != null && ownedText.getOwner() != null) {
                return owner.ordinal() - ownedText.getOwner().ordinal();
            }
        }
        return (type == null ? -1 : type.ordinal()) - (o.getType() == null ? -1 : o.getType().ordinal());
    }


}
