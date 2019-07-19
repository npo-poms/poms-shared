package nl.vpro.domain.gtaa;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@XmlRootElement
public class GTAANewThesaurusObject implements NewThesaurusObject<ThesaurusObject> {

    @Getter
    @Setter
    private String value;

    @Getter
    @Setter
    private List<String> notes;

    @Getter
    @Setter
    private String objectType;

    @lombok.Builder
    public GTAANewThesaurusObject(
        String value,
        @lombok.Singular List<String> notes,
        String objectType) {
        this.value = value;
        this.notes = notes;
        this.objectType = objectType;
    }
}
