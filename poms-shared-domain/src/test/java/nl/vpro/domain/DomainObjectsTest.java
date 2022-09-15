package nl.vpro.domain;

import java.io.File;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainObjectsTest {



    @Test
    public void appendCanonicalFilePathToBuilder() {
        StringBuilder builder = new StringBuilder();
        DomainObjects.appendCanonicalFilePath(1234L, builder);
        assertThat(builder.toString().replace(File.separatorChar, '/')).isEqualTo("12/34/");
    }

}
