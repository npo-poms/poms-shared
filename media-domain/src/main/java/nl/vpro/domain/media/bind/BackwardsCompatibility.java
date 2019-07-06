package nl.vpro.domain.media.bind;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

import org.meeuw.i18n.Region;
import org.meeuw.i18n.countries.Country;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;

import nl.vpro.domain.classification.Term;
import nl.vpro.domain.media.AgeRating;
import nl.vpro.domain.media.Genre;
import nl.vpro.domain.media.MediaClassificationService;
import nl.vpro.domain.media.MisGenreType;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.jackson2.Jackson2Mapper;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
@Slf4j
public class BackwardsCompatibility {

    private static final ThreadLocal<Boolean> v1Compatibility = ThreadLocal.withInitial(() -> false);

    public static void setV1Compatibility(boolean compatibility) {
        v1Compatibility.set(compatibility);
    }

    public static void clearCompatibility() {
        v1Compatibility.remove();
    }

    public static class BroadcasterList {

        public static class Serializer extends AbstractList.Serializer<Broadcaster> {

            @Override
            protected void serializeValue(Broadcaster value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                if (v1Compatibility.get()) {
                    jgen.writeString(value.getDisplayName());
                } else {
                    jgen.writeObject(value);
                }
            }
        }

        public static class Deserializer extends AbstractList.Deserializer<Broadcaster> {

            @Override
            protected Broadcaster deserialize(JsonNode node, DeserializationContext ctxt) throws IOException {
                if (v1Compatibility.get()) {
                    return new Broadcaster(node.textValue());
                } else {
                    return Jackson2Mapper.getInstance().readerFor(Broadcaster.class).readValue(node);
                }
            }
        }
    }

    public static class LanguageList {
        public static class Serializer extends AbstractList.Serializer<Locale> {

            @Override
            protected void serializeValue(Locale value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
                if (v1Compatibility.get()) {
                    jgen.writeString(value.getLanguage().toUpperCase());
                } else {
                    jgen.writeObject(new LocaleWrapper(value));
                }
            }
        }

        public static class Deserializer extends AbstractList.Deserializer<Locale> {

            @Override
            protected Locale deserialize(JsonNode node, DeserializationContext ctxt) throws IOException {
                if (v1Compatibility.get()) {
                    try {
                        return new Locale(node.textValue());
                    } catch (Exception e) {
                        return null;
                    }
                } else {
                    LocaleWrapper wrapper = Jackson2Mapper.getInstance().readerFor(LocaleWrapper.class).readValue(node);
                    return wrapper.getLocale();
                }
            }
        }
    }

    public static class CountryCodeList {
        public static class Serializer extends AbstractList.Serializer<Country> {

            @Override
            protected void serializeValue(Country value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
                if (v1Compatibility.get()) {
                    if (value == null || value.getCode() == null) {
                        log.warn("country code {} is null", value);
                        jgen.writeNull();
                    } else {
                        jgen.writeString(value.getCode());
                    }
                } else {
                    if (value == null) {
                        log.warn("country code is null");
                        jgen.writeNull();
                    } else {
                        jgen.writeObject(new CountryWrapper(value));
                    }
                }

            }
        }

        public static class Deserializer extends AbstractList.Deserializer<Region> {

            @Override
            protected Region deserialize(JsonNode node, DeserializationContext ctxt) throws IOException {
                if (node == null) {
                    return null;
                }
                if (v1Compatibility.get()) {
                    try {
                        return Country.getByCode(node.textValue()).orElse(null);
                    } catch (Exception e) {
                        return null;
                    }
                } else {
                    CountryWrapper wrapper = Jackson2Mapper.getInstance().readerFor(CountryWrapper.class).readValue(node);
                    return wrapper == null ? null : wrapper.getCode();
                }
            }
        }
    }

    public static class GenreSortedSet {
        public static class Serializer extends JsonSerializer<SortedSet<Genre>> {

            @Override
            public void serialize(SortedSet<Genre> genres, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
                jgen.writeStartArray();
                if (v1Compatibility.get()) {
                    for (MisGenreType misGenre : MisGenreType.valueOfGenre(genres)) {
                        jgen.writeString(misGenre.getDisplayName());
                    }
                } else {
                    for (Genre gt : genres) {
                        jgen.writeObject(gt);
                    }
                }

                jgen.writeEndArray();
            }

        }

        public static class Deserializer extends JsonDeserializer<Iterable<Genre>> {

            @Override
            public Iterable<Genre> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
                if(jp.getCodec() == null) {
                    // In org/ektorp/impl/QueryResultParser.java#parseRows(JsonParser jp) it does row.doc.traverse()
                    // traverse() gives a new JsonParser, but without the original Codec. Seems a bug. But this work around it.
                    jp.setCodec(Jackson2Mapper.INSTANCE);
                }

                final SortedSet<Genre> types = new TreeSet<>();

                final ArrayNode array = jp.readValueAs(ArrayNode.class);
                if (v1Compatibility.get()) {



                    List<MisGenreType> misGenres = new ArrayList<>();
                    for (JsonNode jsonNode : array) {
                        misGenres.add(MisGenreType.find(jsonNode.textValue()));
                    }

                    List<Term> terms = MediaClassificationService.getTermsByMisGenreType(misGenres);
                    for(Term term : terms) {
                        types.add(new Genre(term.getTermId()));
                    }
                } else {

                    for (JsonNode jsonNode : array) {
                        Genre type = Jackson2Mapper.getInstance().readerFor(Genre.class).readValue(jsonNode);
                        types.add(type);
                    }
                }

                return types;
            }
        }
    }

    public static class AgeRatingToString {
        public static class Serializer extends nl.vpro.domain.media.bind.AgeRatingToString.Serializer {

            @Override
            public void serialize(AgeRating value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                if (v1Compatibility.get()) {
                    String text = value.toString();
                    if (text.startsWith("_")) {
                        jgen.writeNumber(Short.valueOf(text.substring(1)));
                    } else {
                        jgen.writeNull();
                    }
                } else {
                    super.serialize(value, jgen, provider);
                }

            }
        }

        public static class Deserializer extends nl.vpro.domain.media.bind.AgeRatingToString.Deserializer {

        }

    }

}
