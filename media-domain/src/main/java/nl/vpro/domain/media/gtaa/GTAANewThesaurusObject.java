package nl.vpro.domain.media.gtaa;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vpro.domain.PersonInterface;

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
