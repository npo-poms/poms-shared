/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.profile;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;

import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.page.Page;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlRootElement(name = "profile")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileType", propOrder = {"pageProfile", "mediaProfile"})
@JsonSerialize(using = Profile.Serializer.class)
public class Profile implements Comparable<Profile> {

    @XmlAttribute
    @XmlJavaTypeAdapter(value = InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @Getter
    @Setter
    private Instant timestamp = null;

    @XmlAttribute
    @Getter
    private String name;

    @Getter
    private ProfileDefinition<Page> pageProfile;

    @Getter
    private ProfileDefinition<MediaObject> mediaProfile;

    private Profile() {
    }

    public Profile(String name) {
        this.name = name;
    }

    public Profile(String name, ProfileDefinition<Page> pageProfile, ProfileDefinition<MediaObject> mediaProfile) {
        this.name = name;

        if(pageProfile != null) {
            pageProfile.setProfile(this);
            this.pageProfile = pageProfile;
        }

        if(mediaProfile != null) {
            mediaProfile.setProfile(this);
            this.mediaProfile = mediaProfile;
        }
    }


    @Override
    public String toString() {
        return "Profile{name='" + name + "'}";
    }

    @Override
    public int compareTo(@NonNull Profile o) {
        // inverse ordering on time
        if(o == null) {
            return -1;
        }

        int c = name.compareTo(o.getName());
        if(c != 0) {
            return c;
        }

        return o.getTimestamp().compareTo(timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Profile profile = (Profile)o;

        if(!name.equals(profile.name)) {
            return false;
        }
        if(!timestamp.equals(profile.timestamp)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = timestamp.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    public static class Serializer extends JsonSerializer<Profile> {

        @Override
        public void serialize(final Profile profile, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            //
            jsonGenerator.writeObject(new nl.vpro.domain.api.Error(Response.Status.NOT_IMPLEMENTED.getStatusCode(), "Writing to json unsupported. Please use Accept: application/xml (unsupported by by swagger)") {
                @XmlElement
                String getXml() {
                    StringWriter xml = new StringWriter();
                    JAXB.marshal(profile, xml);
                    return xml.toString();
                }
            });
        }
    }
}
