package nl.vpro.images.tags;

import lombok.Setter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.apache.commons.text.StringEscapeUtils;

import nl.vpro.domain.image.Picture;


@Setter
public class PictureTag extends SourcesTag implements DynamicAttributes {
    private final Map<String, Object> dynamicAttributes = new HashMap<>();
    @Override
    public void doTag() throws IOException {
        Picture picture = image.getSourceSet().getPicture();
        JspWriter writer = getJspContext().getOut();
        writer.print("<picture");
        for (Map.Entry<String, Object> a : dynamicAttributes.entrySet()) {
            writer.print(" ");
            writer.print(a.getKey());
            writer.print("='");
            writer.print(StringEscapeUtils.escapeXml10("" + a.getValue()));
            writer.print("'");
        }
        writer.print(">");
        append(picture);
        writer.print("</picture>");
     }

    @Override
    public void setDynamicAttribute(String uri, String localName, Object value) {
        dynamicAttributes.put(localName, value);
    }
}
