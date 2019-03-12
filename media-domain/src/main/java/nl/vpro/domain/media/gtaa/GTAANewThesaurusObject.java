package nl.vpro.domain.media.gtaa;


import lombok.*;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@XmlRootElement
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
