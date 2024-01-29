package nl.vpro.images.tags;

import lombok.Setter;

import java.io.*;
import java.util.*;

import jakarta.servlet.jsp.tagext.DynamicAttributes;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import nl.vpro.domain.image.ImageMetadata;
import nl.vpro.domain.image.Picture;


@Setter
public class SourcesTag extends SimpleTagSupport implements DynamicAttributes {
    final Map<String, Object> dynamicAttributes = new HashMap<>();

    ImageMetadata image;

    String alt;

    @Override
    public void doTag() throws IOException {
         Picture picture = image.getSourceSet().getPicture();
         append(picture, dynamicAttributes);
    }

    void append(Picture picture, Map<String, Object> dynAttributes) throws IOException {
        Writer writer = getJspContext().getOut();
        for (Picture.Source source : picture.getSourcesList()) {
            writer.append(String.format("<source srcset='%s' type='%s' />", source.getSrcSet(), source.getType()));
        }
        writer.append("<img");
        CharSequence imgTitle = (CharSequence) dynAttributes.remove("title");
        CharSequence effectiveTitle = Optional.ofNullable(imgTitle).orElse(picture.getImageTitle());
        if (StringUtils.isNotBlank(effectiveTitle)) {
            writer.append(String.format(" title='%s'", escape(effectiveTitle)));
        }
        CharSequence effectiveAlt = Optional.ofNullable(alt).orElse(picture.getAlternative());
        if (StringUtils.isNotBlank(effectiveAlt)) {
            writer.append(String.format(" alt='%s'", escape(effectiveAlt)));
        }

        if (picture.getHeight() != null) {
            writer.append(String.format(" width='%d'", picture.getWidth()));
        }
        if (picture.getHeight() != null) {
            writer.append(String.format(" height='%d'", picture.getHeight()));
        }
        appendAttributes(writer, dynAttributes);
        writer.append(" src='%s'".formatted(escape(picture.getImageSrc())));
        writer.append(" />");

    }

    protected void appendAttributes(Writer writer, Map<String, Object> attributes) throws IOException {
        for (Map.Entry<String, Object> a : attributes.entrySet()) {
            writer.append(" %s='%s'".formatted(a.getKey(), escape(a.getValue())));
        }
    }
    String escape(Object s) {
        return StringEscapeUtils.escapeXml10("" + s);
    }

    @Override
    public void setDynamicAttribute(String uri, String localName, Object value) {
        if (localName != null) {
            dynamicAttributes.put(localName, value);
        }
    }


}
