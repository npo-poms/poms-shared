package nl.vpro.domain.media;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;


@XmlRootElement(name = "mediaInformation")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaTableType",
         propOrder = {"programTable",
                      "groupTable",
                      "locationTable",
                      "schedule"})
@lombok.Builder
@AllArgsConstructor
@Slf4j
public class MediaTable implements Iterable<MediaObject> {

    public MediaTable() {

    }

    @XmlElementWrapper(name = "programTable")
    @XmlElement(name = "program")
    protected List<Program> programTable;

    @XmlElementWrapper(name = "groupTable")
    @XmlElement(name = "group")
    protected List<Group> groupTable;

    @XmlElement
    @Getter
    @Setter
    protected LocationTable locationTable;

    @XmlElement
    @Getter
    @Setter
    protected Schedule schedule;

    @XmlAttribute
    @Getter
    @Setter
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    protected Instant publicationTime;

    @XmlAttribute
    @Getter
    @Setter
    protected String publisher;

    @XmlAttribute
    @Getter
    @Setter
    protected Short version;

    @XmlAttribute
    @Getter
    @Setter
    protected String source;


    /**
     * @since 5.9
     */
    public MediaTable add(MediaObject mo) {
        if (mo instanceof Program) {
            return addProgram((Program) mo);
        } else if (mo instanceof Group) {
            return addGroup((Group) mo);
        } else {
            log.warn("Could not add {}", mo);
            return this;
        }
    }

    public MediaTable addProgram(Program program) {
        if(programTable == null) {
            programTable = new ArrayList<>();
        }

        programTable.add(program);
        return this;
    }

    public <T extends MediaObject> Optional<T> find(String mid) {
        for (MediaObject p : Iterables.concat(getProgramTable(), getGroupTable())) {
            if (Objects.equals(p.getMid(), mid)) {
                return Optional.of((T) p);
            }
        }
        return Optional.empty();
    }

    public <T extends MediaObject> Optional<T> findByCrid(String crid) {
        for (MediaObject p : Iterables.concat(getProgramTable(), getGroupTable())) {
            if (p.getCrids().contains(crid)) {
                return Optional.of((T) p);
            }
        }
        return Optional.empty();
    }

    /**
     * @since 5.9
     */
    public boolean contains(String mid) {
        return find(mid).isPresent();
    }

    public List<Program> getProgramTable() {
        if(programTable == null) {
            programTable = new ArrayList<>();
        }
        return programTable;
    }

    public void setProgramTable(List<Program> programTable) {
        this.programTable = programTable;
    }

    public MediaTable addGroup(Group group) {
        if(groupTable == null) {
            groupTable = new ArrayList<>();
        }

        groupTable.add(group);
        return this;
    }

    public List<Group> getGroupTable() {
        if(groupTable == null) {
            groupTable = new ArrayList<>();
        }
        return this.groupTable;
    }

    public void setGroupTable(List<Group> groupTable) {
        this.groupTable = groupTable;
    }


    @Override
    public String toString() {
        return "MediaTable " + getGroupTable().size() + " groups " + getProgramTable().size() + " program " + getSchedule();
    }

    @Nonnull
    @Override
    public Iterator<MediaObject> iterator() {
        return Iterators.concat(
            getProgramTable().listIterator(),
            getGroupTable().listIterator()
        );
    }
}
