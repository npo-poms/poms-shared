package nl.vpro.domain.media;

import nl.vpro.domain.media.gtaa.GTAARecord;
import nl.vpro.domain.media.gtaa.GTAAStatus;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TopicTest {

    @SuppressWarnings("SimplifiableJUnitAssertion")
    @Test
    public void testEquals() {

        Topic topic1 = createTestTopic();
        Topic topic2 = createTestTopic();
        Topic topic3 = createTestTopic();
        Topic topic4 = createTestTopic();
        Topic topic5 = createTestTopic();
        Topic topic6 = createTestTopic();
        Topic topic7 = createTestTopic();

        topic1.setId(123L);
        topic3.setName("bla");
        topic4.setScopeNotes(Collections.singletonList("oops"));
        topic5.setGtaaUri(URI.create("http://bla.bla.nl/gtaa/31182"));
        topic6.setScopeNotes(new ArrayList<>());
        topic7.setId(123L);
        topic7.setName("blablabla");

        //noinspection EqualsWithItself
        assertTrue(topic1.equals(topic1));
        assertTrue(topic1.equals(topic2));
        assertFalse(topic1.equals(topic3));
        assertFalse(topic1.equals(topic4));
        assertFalse(topic1.equals(topic5));
        assertFalse(topic1.equals(topic6));
        //noinspection ConstantConditions
        assertFalse(topic1.equals(null));
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(topic1.equals("topic"));
        assertTrue(topic1.equals(topic7));
    }

    @Test
    public void testHashCode() {

        Topic topic1 = createTestTopic();
        Topic topic2 = createTestTopic();
        topic2.setGtaaStatus(GTAAStatus.deleted);

        assertEquals(topic1.hashCode(), topic2.hashCode());
    }

    @Test
    public void testToString() {

        Topic topic = createTestTopic();
        assertEquals("Topic[name=kattenkwaad,scopeNotes=[blabla],gtaa_uri=http://data.beeldengeluid.nl/gtaa/31182]",
                topic.toString());
    }

    @Test
    public void testCompareTo() {

        Topic topic1 = createTestTopic();
        Topic topic2 = createTestTopic();
        Topic topic3 = createTestTopic();

        topic2.setName(topic1.getName() + "1");
        topic3.setGtaaUri(URI.create(topic1.getGtaaUri().toString() + "1"));

        //noinspection EqualsWithItself
        assertEquals(0, topic1.compareTo(topic1));
        assertEquals(-1, topic1.compareTo(topic2));
        assertEquals(1, topic2.compareTo(topic1));
        assertEquals(-1, topic1.compareTo(topic3));
        assertEquals(1, topic3.compareTo(topic1));
    }

    @Test
    public void testClone() {

        Topic topic = createTestTopic();

        Topic clonedTopic = topic.clone();

        assertNull(clonedTopic.getId());
        assertEquals(topic.getParent(), clonedTopic.getParent());
        assertEquals(topic.getGtaaRecord(), clonedTopic.getGtaaRecord());
    }

    private Topic createTestTopic() {

        Topic topic = new Topic();
        topic.setParent(createTestTopics(topic));
        topic.setGtaaRecord(createTestGtaaRecord());
        return topic;
    }

    private Topics createTestTopics(Topic topic) {

        Topics topics = new Topics();
        topics.setParent(null);
        topics.setValues(Collections.singletonList(topic));
        return topics;
    }

    private GTAARecord createTestGtaaRecord() {

        GTAARecord gtaaRecord = GTAARecord.builder()
                .name("kattenkwaad")
                .scopeNotes(Collections.singletonList("blabla"))
                .uri(URI.create("http://data.beeldengeluid.nl/gtaa/31182"))
                .status(GTAAStatus.approved)
                .build();

        gtaaRecord.setId(100L);
        return gtaaRecord;
    }
}