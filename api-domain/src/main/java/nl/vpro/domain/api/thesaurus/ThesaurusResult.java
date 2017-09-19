package nl.vpro.domain.api.thesaurus;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Builder;
import lombok.NoArgsConstructor;

import nl.vpro.domain.media.gtaa.GTAAPerson;
import nl.vpro.domain.media.gtaa.ThesaurusObject;
import nl.vpro.domain.api.Result;

@XmlRootElement(name = "thesaurusItems")
@XmlType(name = "thesaurusItemsType")
@NoArgsConstructor
public class ThesaurusResult<T extends ThesaurusObject> extends Result<T> {

    public ThesaurusResult(List<T> list, Integer max) {
        super(list, 0L, max, null);
    }



    public static class PersonList extends ThesaurusResult<GTAAPerson> {

        protected PersonList() {

        }

        @Builder
        public PersonList(List<GTAAPerson> list,  Integer max) {
            super(list, max);
        }
    };







}
