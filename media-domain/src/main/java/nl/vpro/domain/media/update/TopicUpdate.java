package nl.vpro.domain.media.update;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;

import nl.vpro.domain.media.Topic;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "topicUpdateType")
@XmlRootElement(name = "topic")
public class TopicUpdate {

    @XmlAttribute(required = true)
    @Getter
    private String gtaaUri;

    public TopicUpdate(String gtaaUri) {
        this.gtaaUri = gtaaUri;
    }

    public TopicUpdate(Topic topic) {
        this(topic.getGtaaUri());
    }

    public TopicUpdate() {
        // needed for jaxb
    }

    public Topic toTopic() {
        return Topic.builder().uri(gtaaUri).build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("gtaaUri", gtaaUri)
            .toString();
    }
}
