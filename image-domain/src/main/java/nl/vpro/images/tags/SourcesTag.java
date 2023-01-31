package nl.vpro.images.tags;

import lombok.Setter;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Optional;

import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import nl.vpro.domain.image.ImageMetadata;
import nl.vpro.domain.image.Picture;


@Setter
public class SourcesTag extends SimpleTagSupport  {
    ImageMetadata image;
    CharSequence imgStyle;
    CharSequence alt;
    @Override
    public void doTag() throws IOException {
         Picture picture = image.getSourceSet().getPicture();
         append(picture);
     }

    void append(Picture picture) throws IOException {
         Writer writer = getJspContext().getOut();
        for (Map.Entry<String, String> e : picture.getSources().entrySet()) {
            writer.append(String.format("<source srcset='%s' type='%s' />", e.getValue(), e.getKey()));
        }
        writer.append("<img");
        if (StringUtils.isNotBlank(imgStyle)) {
            writer.append(String.format(" style='%s'", StringEscapeUtils.escapeXml10(imgStyle.toString())));
        }
        CharSequence effectiveAlt = Optional.ofNullable(alt).orElse(picture.getAlternative());
        if (StringUtils.isNotBlank(effectiveAlt)) {
            writer.append(String.format(" alt='%s'", StringEscapeUtils.escapeXml10(effectiveAlt.toString())));
        }
        if (picture.getHeight() != null) {
            writer.append(String.format(" width='%d'", picture.getWidth()));
        }
        if (picture.getHeight() != null) {
            writer.append(String.format(" height='%d'", picture.getHeight()));
        }
        writer.append(String.format(" src='%s'", StringEscapeUtils.escapeXml10(picture.getImageSrc())));
        writer.append(" />");

    }


}
