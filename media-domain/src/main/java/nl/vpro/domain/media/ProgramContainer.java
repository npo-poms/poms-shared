package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"program"})
public class ProgramContainer implements Serializable {
    @Serial
    private static final long serialVersionUID = -2554132753283588255L;

    @Getter
    @Setter
    private Program program;

    public ProgramContainer() {
    }

    public ProgramContainer(Program p) {
        this.program = p;
    }
}
