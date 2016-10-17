package nl.vpro.domain.media.search;

import java.util.Collection;

import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.MediaType;
import nl.vpro.domain.media.Net;
import nl.vpro.domain.user.Editor;

/**
 * This form is currently used by the MediaRepublisher, and is implemented using the criteria api. Is has a lot of similarity with {@link MediaForm}, and we could perhaps merge them, and have one generic search form which we could use in any case.
 *
 * @author Michiel Meeuwissen
 * @since 2.1
 */
public class RepublishMediaForm {

    private Collection<MediaType> types;

    private Editor createdBy;

    private DateRange lastPublishedRange;

    private Collection<Channel> channels;

    private Collection<Net> nets;

    private Collection<String> ids;

    private Long offset;

    private Integer max;

    public DateRange getLastPublishedRange() {
        return lastPublishedRange;
    }

    public void setLastPublishedRange(DateRange range) {
        this.lastPublishedRange = range;
    }

    public boolean hasLastPublishedRange() {
        return lastPublishedRange != null;
    }

    public boolean hasTypes() {
        return types != null && types.size() > 0 && !types.contains(MediaType.MEDIA);
    }

    public Collection<MediaType> getTypes() {
        return types;
    }

    public void setTypes(Collection<MediaType> types) {
        this.types = types;
    }

    public Editor getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Editor createdBy) {
        this.createdBy = createdBy;
    }

    public boolean hasCreatedBy() {
        return createdBy != null;
    }

    public Collection<Channel> getChannels() {
        return channels;
    }

    public void setChannels(Collection<Channel> channels) {
        this.channels = channels;
    }

    public boolean hasChannels() {
        return channels != null && !channels.isEmpty();
    }

    public Collection<Net> getNets() {
        return nets;
    }

    public void setNets(Collection<Net> nets) {
        this.nets = nets;
    }

    public boolean hasNets() {
        return nets != null && !nets.isEmpty();
    }

    public Collection<String> getIds() {
        return ids;
    }

    public void setIds(Collection<String> ids) {
        this.ids = ids;
    }

    public boolean hasIds() {
        return ids != null && !ids.isEmpty();
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public boolean hasOffset() {
        return offset != null;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public boolean hasMax() {
        return max != null;
    }
}
