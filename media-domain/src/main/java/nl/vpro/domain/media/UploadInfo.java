package nl.vpro.domain.media;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@lombok.Builder
public class UploadInfo {
    @Id
    @Column(nullable = false)
    private UUID id = UUID.randomUUID();

    @Column
    private String mid;

    @Column
    private String fileName;

    @Column
    private long fileSize;


    @Column
    private String aspectRatio;


    protected UploadInfo() {

    }
}
