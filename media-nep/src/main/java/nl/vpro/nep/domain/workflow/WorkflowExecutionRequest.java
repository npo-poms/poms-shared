package nl.vpro.nep.domain.workflow;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

@lombok.Builder(builderClassName = "Builder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class WorkflowExecutionRequest implements Serializable {

    private String mid;
    private String filename;
    private EncryptionType encryption;
    private PriorityType priority;
    @lombok.Builder.Default
    private Type type = Type.VIDEO;
    @lombok.Builder.Default
    private List<String> platforms = new ArrayList<>();

    public WorkflowExecutionRequest() {
    }

    public static class Builder {

        /**
         * Sets the file name in the request, optionally prefixing it with the given ftp user name
         */
        public Builder file(@Nullable String ftpUsername, String fileName) {
            if (fileName != null) {
                if (fileName.startsWith("/") || ftpUsername == null) {
                    return filename(fileName.substring(1));
                } else {
                    return filename(ftpUsername + "/" + fileName);
                }
            }
            return this;
        }

    }

}
