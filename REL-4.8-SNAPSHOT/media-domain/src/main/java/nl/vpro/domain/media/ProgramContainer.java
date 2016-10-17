package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"program"})
public class ProgramContainer {
    private Program program;

    public ProgramContainer() {
    }

    public ProgramContainer(Program p) {
        this.program = p;
    }
    public Program getProgram() {
        return program;
    }
    public void setProgram(Program program) {
        this.program = program;
    }
}
