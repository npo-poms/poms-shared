package nl.vpro.domain.media.gtaa;


import lombok.*;
import nl.vpro.domain.PersonInterface;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@XmlRootElement
@Builder
public class GTAANewThesaurusObject {

    @Getter
    @Setter
    private String value;

    @Getter
    @Setter
    private String note;

    @Getter
    @Setter
    private String objectType;

    @lombok.Builder
    public GTAANewThesaurusObject(String value, String note, String objectType) {
        this.value = value;
        this.note = note;
        this.objectType = objectType;
    }
}
