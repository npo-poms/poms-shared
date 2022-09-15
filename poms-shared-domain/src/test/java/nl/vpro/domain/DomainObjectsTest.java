package nl.vpro.domain;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainObjectsTest {

    @Test
    public void appendCanonicalFilePathToBuilder() {
        StringBuilder builder = new StringBuilder();
        DomainObjects.appendCanonicalFilePath(1234, builder);
        assertThat(builder.toString().replace(File.separatorChar, '/')).isEqualTo("/12/34");
    }



    @Test
    public void appendCanonicalFilePathToPath() {
        Path path = Paths.get("foobar");
        path = DomainObjects.appendCanonicalFilePath(12345, path);
        assertThat(path).isEqualTo(Paths.get("foobar", "12", "34", "5"));
    }

}
