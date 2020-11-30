package nl.vpro.domain.api.media;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.vpro.domain.api.MediaChange;
import nl.vpro.domain.api.profile.ProfileDefinition;
import nl.vpro.domain.media.MediaObject;

import static org.mockito.Mockito.mock;

/**
 * TODO Currently tested in api-backend-media with nl.vpro.domain.api.media.ChangeIteratorWithCouchdbTest
 *
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public class ChangeIteratorTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    private final ProfileDefinition<MediaObject> current = mock(ProfileDefinition.class);

    @SuppressWarnings("unchecked")
    private final ProfileDefinition<MediaObject> previous = mock(ProfileDefinition.class);

    private List<MediaChange> nodes;

/*

    @Test
    public void testNextOutput() throws Exception {
        Change document = Change.of(mapper.reader().readValue(delete()), 10);
        nodes = Collections.singletonList(document);

        ChangeIterator iterator = new ChangeIterator(nodes.iterator(), 10L, null, null);
            assertThat(iterator.hasNext()).isTrue();

            Change change = iterator.next();
            assertThat(change.getSequence()).isEqualTo(15);
            assertThat(change.getRevision()).isEqualTo(97);
            assertThat(change.getMedia()).isNull();
            assertThat(change.isDeleted()).isTrue();

            assertThat(iterator.hasNext()).isFalse();
        }

        @Test(expected = NoSuchElementException.class)
        public void testNextOnNoSuchElementException() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(update(15)));
            nodes = Collections.singletonList(document);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 10L, null, null);
            assertThat(iterator.hasNext()).isTrue();
            iterator.next();
            iterator.next();
        }

        @Test
        public void testHasNextOnRepeatedCalls() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(update(15)));
            nodes = Collections.singletonList(document);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 10L, null, null);
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.next()).isNotNull();
            assertThat(iterator.hasNext()).isFalse();
        }

        @Test
        public void testNextOnUpdateNoProfiles() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(update(15)));
            nodes = Collections.singletonList(document);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 10L, null, null);
            assertThat(iterator.hasNext()).isTrue();
        }

        @Test
        public void testNextOnUpdateBeforeSinceNoProfiles() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(update(15)));
            nodes = Collections.singletonList(document);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 18L, null, null);
            assertThat(iterator.hasNext()).isFalse();
        }

        @Test
        public void testNextOnMatchingUpdateWhenNoPreviousProfile() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(update(15)));
            nodes = Collections.singletonList(document);

            when(current.test(any(MediaObject.class))).thenReturn(true);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 10L, current, null);
            assertThat(iterator.hasNext()).isTrue();
        }

        @Test
        public void testNotApplyingUpdateWhenNoPreviousProfileAndSinceBeforeUpdate() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(update(15)));
            nodes = Collections.singletonList(document);

            when(current.test(any(MediaObject.class))).thenReturn(false);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 10L, current, null);
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.next().isDeleted()).isTrue();
        }

        @Test
        public void testNotapplyingUpdateWhenNoPreviousProfileAndUpdateAndSinceAfterUpdate() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(update(15)));
            nodes = Collections.singletonList(document);

            when(current.test(any(MediaObject.class))).thenReturn(false);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 18L, current, null);
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.next().isDeleted()).isTrue();
        }

        @Test
        public void testNextOnUpdateForFirstPublication() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(update(15)));
            nodes = Collections.singletonList(document);

            when(current.test(any(MediaObject.class))).thenReturn(true);
            when(previous.test(any(MediaObject.class))).thenReturn(false);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 18L, current, previous);
            assertThat(iterator.hasNext()).isTrue();
        }

        @Test
        public void testNextOnDeleteForFirstPublication() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(delete()));
            nodes = Collections.singletonList(document);

            when(current.test(any(MediaObject.class))).thenReturn(true);
            when(previous.test(any(MediaObject.class))).thenReturn(false);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 1L, current, previous);
            assertThat(iterator.hasNext()).isTrue(); // Deletes are always returned now.
        }

        @Test
        public void testNextOnUpdateForRepublication() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(update(15)));
            nodes = Collections.singletonList(document);

            when(current.test(any(MediaObject.class))).thenReturn(true);
            when(previous.test(any(MediaObject.class))).thenReturn(false);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 10L, current, previous);
            assertThat(iterator.hasNext()).isTrue();
        }

        @Test
        // NPA-256
        public void testNextOnUpdateForRepublicationNotInBothProfiles() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(update(5)));
            nodes = Collections.singletonList(document);

            when(current.test(any(MediaObject.class))).thenReturn(false);
            when(previous.test(any(MediaObject.class))).thenReturn(false);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 15L, current, previous);
            assertThat(iterator.hasNext()).isFalse();
        }

        @Test
        public void testNextOnDeleteWithoutDocument() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(delete()));
            nodes = Collections.singletonList(document);

            when(current.test(any(MediaObject.class))).thenReturn(true);
            when(previous.test(any(MediaObject.class))).thenReturn(false);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 10L, current, previous);
            assertThat(iterator.hasNext()).isTrue(); // deletes without document always match
        }

        @Test
        public void testNextOnDeleteWithDocumentForRepublication() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(deleteWithDocument(15)));
            nodes = Collections.singletonList(document);

            when(current.test(any(MediaObject.class))).thenReturn(true);
            when(previous.test(any(MediaObject.class))).thenReturn(false);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 10L, current, previous);
            assertThat(iterator.hasNext()).isFalse(); // it didn't apply, it is now deleted, but it doesn't matter any more
        }

        @Test
        public void testNextOnFilteredUpdateWithRevocation() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(update(15)));
            nodes = Collections.singletonList(document);

            when(current.test(any(MediaObject.class))).thenReturn(false);
            when(previous.test(any(MediaObject.class))).thenReturn(true);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 18L, current, previous);
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.next().isDeleted()).isTrue();
        }

        @Test
        public void testNextOnFilteredUpdateNeverPublished() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(update(15)));
            nodes = Collections.singletonList(document);

            when(current.test(any(MediaObject.class))).thenReturn(false);
            when(previous.test(any(MediaObject.class))).thenReturn(true);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 12L, current, previous);
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.next().isDeleted()).isTrue();
        }

        @Test
        public void testNextOnFilteredUpdateWhenNeverPublished() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(update(15)));
            nodes = Collections.singletonList(document);

            when(current.test(any(MediaObject.class))).thenReturn(false);
            when(previous.test(any(MediaObject.class))).thenReturn(false);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 10L, current, previous);
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.next().isDeleted()).isTrue();
        }

        @Test
        public void testNextOnFilteredDeleteWhenPreviouslyPublished() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(delete()));
            nodes = Collections.singletonList(document);

            when(current.test(any(MediaObject.class))).thenReturn(false);
            when(previous.test(any(MediaObject.class))).thenReturn(true);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 12L, current, previous);
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.next().isDeleted()).isTrue();
        }

        @Test
        public void testNextOnFilteredDeleteWithDocumentWhenNeverPublished() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(deleteWithDocument(15)));
            nodes = Collections.singletonList(document);

            when(current.test(any(MediaObject.class))).thenReturn(false);
            when(previous.test(any(MediaObject.class))).thenReturn(false);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 1L, current, previous);
            assertThat(iterator.hasNext()).isFalse();
        }

        @Test
        public void testNextFilteredOnDeleteWhenRepublished() throws Exception {
            DocumentChange document = new StdDocumentChange(mapper.readTree(delete()));
            nodes = Collections.singletonList(document);

            when(current.test(any(MediaObject.class))).thenReturn(false);
            when(previous.test(any(MediaObject.class))).thenReturn(true);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 10L, current, previous);
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.next().isDeleted()).isTrue();
        }

        @Test
        public void testSequenceSimple() throws Exception {
            DocumentChange document1 = new StdDocumentChange(mapper.readTree(update(15)));
            DocumentChange document2 = new StdDocumentChange(mapper.readTree(update(16)));
            nodes = Arrays.asList(document1, document2);

            when(current.test(any(MediaObject.class))).thenReturn(true*/
/*15*//*
, false*/
/*16*//*
);
            when(previous.test(any(MediaObject.class))).thenReturn(false);

            ChangeIterator iterator = new ChangeIterator(new DocumentChangeWrapper(nodes.iterator()), 10L, current, previous);
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.next().getSequence()).isEqualTo(15); // next doesn't apply
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.next().isDeleted()).isTrue();
        }

        private String delete() {
            return "{\n" +
                "   \"seq\" : 15,\n" +
                "   \"id\" : \"POMS_S_NOS_223583\",\n" +
                "   \"changes\" :\n" +
                "      [\n" +
                "         {\n" +
                "            \"rev\" : \"97-4719d488577c0ad31d853e90341f52cb\"\n" +
                "         }\n" +
                "      ],\n" +
                "   \"deleted\": true,\n" +
                "   \"doc\" :\n" +
                "      {\n" +
                "         \"_id\" : \"POMS_S_NOS_223583\",\n" +
                "         \"_rev\" : \"97-4719d488577c0ad31d853e90341f52cb\",\n" +
                "         \"_deleted\" : true\n" +
                "      }" +
                "}";
        }

        private String update(int seq) {
            return "{\n" +

                "         }\n" +
                "      ],\n" +
                "   \"doc\" :\n" +
                "      {\n" +
                "         \"_id\" : \"POMS_S_NOS_223583\",\n" +
                "         \"_rev\" : \"97-4719d488577c0ad31d853e90341f52cb\",\n" +
                "         \"objectType\" : \"group\",\n" +
                "         \"mid\" : \"POMS_S_NOS_223583\",\n" +
                "         \"avType\" : \"AUDIO\"\n" +
                "      }" +
                "}";
        }

        private String deleteWithDocument(int seq) { // we don't find this in couchdb but if it could be implemented it could work better.
            return "{\n" +
                "   \"seq\" : " + seq + ",\n" +
                "   \"id\" : \"POMS_S_NOS_223583\",\n" +
                "   \"changes\" :\n" +
                "      [\n" +
                "         {\n" +
                "            \"rev\" : \"97-4719d488577c0ad31d853e90341f52cb\"\n" +
                "         }\n" +
                "      ],\n" +
                "   \"deleted\": true,\n" +
                "   \"doc\" :\n" +
                "      {\n" +
                "         \"_id\" : \"POMS_S_NOS_223583\",\n" +
                "         \"_rev\" : \"97-4719d488577c0ad31d853e90341f52cb\",\n" +
                "         \"_deleted\" : true,\n" +
                "         \"objectType\" : \"group\",\n" +
                "         \"mid\" : \"POMS_S_NOS_223583\",\n" +
                "         \"avType\" : \"AUDIO\"\n" +
                "      }" +
                "}";
        }
    }
*/


}
