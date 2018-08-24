package nl.vpro.rs.pages.update;

import lombok.Getter;

import java.util.concurrent.CompletableFuture;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@Getter
public class DeleteResult {
    @XmlTransient
    final CompletableFuture<?> future;
    final int count;
    final int notallowedCount;
    final boolean success = true;

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

