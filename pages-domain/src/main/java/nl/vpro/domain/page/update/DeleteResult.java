package nl.vpro.domain.page.update;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
@Slf4j
public class DeleteResult {
    @XmlTransient
    final CompletableFuture<?> future;
    @XmlAttribute
    private Integer count;
    @XmlAttribute
    private Integer notallowedCount;
    @XmlAttribute
    private Integer alreadyDeletedCount;

    @XmlAttribute
    private boolean success = true;
    @XmlValue
    private String message;

    private DeleteResult() {
        this.future = CompletableFuture.completedFuture(null);
        // just for silly jaxb
    }

    @lombok.Builder
    private DeleteResult( CompletableFuture<?> future, int count, int notallowedCount, int alreadyDeletedCount,  Boolean success, String message) {
        this.future = future == null ? CompletableFuture.completedFuture(null) : future;
        this.count = count;
        this.notallowedCount = notallowedCount;
        this.alreadyDeletedCount = alreadyDeletedCount;
        this.success = success == null || success;
        this.message = message;
    }

    @JsonCreator
    protected DeleteResult(
        @JsonProperty("count") Integer count,
        @JsonProperty("notallowedCount") Integer notallowedCount) {
        this.count = count;
        this.notallowedCount = notallowedCount;
        this.future = CompletableFuture.completedFuture(null);
    }

    @Override
    public String toString() {
        return "Deleted " + count +  " " + (notallowedCount > 0 ? " (not allowed : " + notallowedCount + ")" : "") + (future != null && ! future.isDone() ? " (still running) " : "");
    }

    public DeleteResult and(DeleteResult result) {
        return DeleteResult.builder()
            .count(this.count + result.getCount())
            .notallowedCount(this.notallowedCount + result.getNotallowedCount())
            .future(this.future.thenApply((v) -> {
                try {
                    return result.getFuture().get();
                } catch (ExecutionException ee) {
                    log.error(ee.getMessage());
                    return null;
                } catch (InterruptedException iae) {
                    log.info(iae.getMessage());
                    Thread.currentThread().interrupt();
                    return null;
                }

            }))
            .build();
    }

}

