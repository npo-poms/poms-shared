package nl.vpro.domain.gtaa;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

import nl.vpro.openarchives.oai.Label;
import nl.vpro.w3.rdf.Description;

@GTAAScheme(Scheme.name)
public class GTAAName extends AbstractThesaurusItem {

    @Builder
    private GTAAName(String id,
                     String value,
                     List<Label> notes,
                     Status status,
                     String redirectedFrom,
                     Instant modified){
        this.setId(id);
        this.setValue(value);
        this.setNotes(notes);
        this.setStatus(status);
        this.setRedirectedFrom(redirectedFrom);
        this.setLastModified(modified);
    }

    private GTAAName(){}

    public static GTAAName create(Description description) {
        final GTAAName answer = new GTAAName();
        fill(description, answer);
        return answer;
    }


}
