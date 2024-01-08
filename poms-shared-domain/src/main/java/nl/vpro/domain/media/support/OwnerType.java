package nl.vpro.domain.media.support;

import java.util.*;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import org.checkerframework.checker.nullness.qual.PolyNull;

import nl.vpro.domain.Xmlns;
import nl.vpro.i18n.Displayable;


/**
 * An owner is a data supplier and authority which can be held responsible for parts of the
 * media data set. {@link #MIS} for example is responsible for all schedule data and some
 * parts of the media meta-data.
 * <p/>
 * When a field gets an owner and an 1..n to n association, different owners can supply there
 * own value for this field to be kept for future usage and updates.
 * <p/>
 * The order of the values for this Enum is relevant because it is responsible for the ordering of Ownable types
 * when obtained from data or send to clients.
 * <p/>
 * Owners should not be confused with users or groups. They don't take any part in security
 * related scenarios.
 *
 * @author roekoe
 * @since 0.4
 */
@XmlEnum
@XmlType(name = "ownerTypeEnum", namespace = Xmlns.SHARED_NAMESPACE)
public enum OwnerType implements Displayable {

    /**
     * Represents data coming from a user or system working for a broadcaster that is allowed
     * to update an Ownable entity.
     */
    BROADCASTER("Omroep"),


    /**
     * Represents Ownable data updated by NEBO batch updates. Before version 1.4 we did not distinguish between NEBO
     * and Ceres as different sources. There might be legacy data incorrectly revering to NEBO as its owner.
     * @deprecated NEBO doesn't exist anymore
     */
    @Deprecated
    NEBO("Nebo") {
        @Override
        public boolean display() {
            return false;
        }
    },

    /**
     * MSE-3358
     * @since 4.7
     */
    NPO("NPO"),

    /**
    /**
     * Represents Ownable data updated by MIS via TVAnytime deliveries
     */
    MIS("MIS"),

    /**
     * Represents Ownable data updated by CERES via predict, metadata, notify or revoke file delivery. It is possible
     * that data obtained from CERES indirectly results from NEBO, MIS or What's-On, but we can't detect these origins
     * from a Ceres delivery.
     *
     * @since 1.4
     */
    CERES("Ceres"),

     /**
     * Represents Ownable data updated by PLUTO via predict, metadata, notify or revoke file delivery. It is possible
     * that data obtained from PLUTO indirectly results from NEBO, MIS or What's-On, but we can't detect these origins
     * from a Pluto delivery.
     *
     * @since 3.4
     */
    PLUTO("Pluto"),

    /**
     * Represents Ownable data updated by PLUTO via predict, metadata, notify or revoke file delivery. It is possible
     * that data obtained from PLUTO indirectly results from NEBO, MIS or What's-On, but we can't detect these origins
     * from a Pluto delivery.
     *
     * @since 3.4
     */
    PROJECTM("Project M"),

    /**
     * Represents Ownable data from What's-On send to Poms by Ceres. Twice a day Ceres queries What's-On for the
     * complete NED1/2/3 schedules. The output from these queries is the first source for media and schedule
     * meta-data in Poms and will later on be updated by sources more upstream.
     *
     * @since 1.4
     */
    WHATS_ON("Whats'On"),

    /**
     * Represents Ownable data from Immix ("Beeld en Geluid") send to Poms via updates from their archive.
     *
     * @since 1.4
     * @deprecated Use {@link #BEELDENGELUID}
     */
    @Deprecated
    IMMIX("Immix") {
        @Override
        public boolean display() {
            return false;
        }
    },


    /**
     * Represents ownable data from an otherwise unspecified authoritative backend system. E.g. Prepr, NEP.
     * @since 5.2
     */
    AUTHORITY("Autoritatieve bron"),

    /**
     * Represents Ownable data from Radiobox. Radiobox delivers schedules and metadata for RAD1/2/3/4/5/6/FUNX
     *
     * @since 1.6
     * @deprecated  Radiobox is replaced by Prepr. See {@link #AUTHORITY}
     */
    @Deprecated
    RADIOBOX("Radiobox 2") {
        @Override
        public boolean display() {
            return false;
        }
    },

    /**
     * @since 5.3
     */
    BEELDENGELUID("Beeld & Geluid"),


    /**
     * This textual value is created temporarily, and may be discarded as soon something better is available.
     * It should probably only exist on unpublished objects.
     * @since 7.10
     */
    TEMPORARY("Tijdelijk") {
        @Override
        public boolean display() {
            return false;
        }
    }

    ;
    private final String displayName;

    private final boolean isDeprecated;

    OwnerType(String displayName) {
        this.displayName = displayName;
        boolean dep;
        try {
            dep = OwnerType.class.getField(name()).isAnnotationPresent(Deprecated.class);
        } catch (Exception e) {
            dep = false;
        }
        isDeprecated = dep;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public boolean isDeprecated() {
        return isDeprecated;
    }

    public static Comparator<OwnerType> comparator(OwnerType... order) {
        final List<OwnerType> list = Arrays.asList(order);
        return new Comparator<OwnerType>() {
            @Override
            public int compare(OwnerType o1, OwnerType o2) {
                return ord(o1) - ord(o2);
            }
            int ord(OwnerType o) {
                int i = list.indexOf(o);
                if (i < 0) {
                    i = order.length + o.ordinal();
                }
                return i;
            }
        };
    }

    public static OwnerType[] after(OwnerType ownerType) {
        return Arrays.stream(OwnerType.values()).filter(ot -> ot.ordinal() >= ownerType.ordinal()).toArray(OwnerType[]::new);
    }
    public static OwnerType last() {
        return OwnerType.values()[OwnerType.values().length - 1];
    }

    public static OwnerType first() {
        return OwnerType.values()[0];
    }

    public static OwnerType down(OwnerType ot) {
        return OwnerType.values()[ot.ordinal() + 1];
    }

    /**
     * the owner types that are relevant to {TextualObjects#}
     */
    public static final List<OwnerType> ENTRIES = Collections.unmodifiableList(Arrays.asList(BROADCASTER, NPO));


    @PolyNull
    public static OwnerType valueOfNullable(@PolyNull String value){
        if (value == null) {
            return null;
        }
        return valueOf(value);

    }
}
