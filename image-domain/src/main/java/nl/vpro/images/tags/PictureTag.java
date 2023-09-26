package nl.vpro.images.tags;

import lombok.Setter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.DynamicAttributes;

import nl.vpro.domain.image.Picture;


@Setter
public class PictureTag extends SourcesTag implements DynamicAttributes {


    @Override
    public void doTag() throws IOException {
        Picture picture = image.getSourceSet().getPicture();
        JspWriter writer = getJspContext().getOut();
        writer.print("<picture");
        appendAttributes(writer,
            dynamicAttributes.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> ! e.getKey().startsWith("img."))
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (x, y) -> y,
                    LinkedHashMap::new)
                )
        );

        writer.print(">");
        append(picture, dynamicAttributes.entrySet().stream()
            .filter(e -> e.getKey() != null)
            .filter(e -> e.getKey().startsWith("img."))
            .collect(Collectors.toMap(
                e -> e.getKey().substring(4),
                Map.Entry::getValue,
                (x, y) -> y,
                LinkedHashMap::new)
            )
        );
        writer.print("</picture>");
     }


}
