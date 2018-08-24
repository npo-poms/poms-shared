package nl.vpro.domain.page.update;

import lombok.Getter;
import lombok.ToString;

import java.util.concurrent.CompletableFuture;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@XmlRootElement(name = "deleteresult")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@ToString(of = {"success", "count", "notallowedCount"})
public class DeleteResult {
    @XmlTransient
    final CompletableFuture<?> future;
    @XmlAttribute
    private int count;
    @XmlAttribute
    private int notallowedCount;
    @XmlAttribute
    private boolean success = true;

    private DeleteResult() {
        this.future = CompletableFuture.completedFuture(null);
        // just for silly jaxb
    }

    @lombok.Builder
    public DeleteResult( CompletableFuture<?> future, Integer count, Integer notallowedCount) {
        this.future = future;
        this.count = count == null ? 1 : count;
        this.notallowedCount = notallowedCount == null ? 0 : notallowedCount;
    }

    @JsonCreator
    protected DeleteResult(
        @JsonProperty("count") Integer count,
        @JsonProperty("notallowedCount") Integer notallowedCount) {
        this.count = count;
        this.notallowedCount = notallowedCount;
        this.future = CompletableFuture.completedFuture(null);

    }
}

