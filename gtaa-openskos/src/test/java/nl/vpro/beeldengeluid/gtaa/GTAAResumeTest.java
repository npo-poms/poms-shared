package nl.vpro.beeldengeluid.gtaa;

import javax.xml.bind.JAXB;

import org.junit.jupiter.api.Test;


import nl.vpro.openarchives.oai.ListRecord;
import nl.vpro.openarchives.oai.ResumptionToken;

import static org.assertj.core.api.Assertions.assertThat;

public class GTAAResumeTest {

    private OpenskosRepository gtaaRepository = new OpenskosRepository("", "", null) {
        @Override
        protected <T> T getForPath(final String path, final Class<T> tClass) {
            if (path.contains("resumptionToken")) {
                return JAXB.unmarshal(getClass().getResourceAsStream("/oai-pmh-resume.xml"), tClass);
            } else {
                return null;
            }
        }
    };

    @Test
    public void testResume() {
        ListRecord listRecord = gtaaRepository.getUpdates(new ResumptionToken());
        assertThat(listRecord).isNotNull();
        assertThat(listRecord.getRecords()).isNotNull();
        assertThat(listRecord.getRecords()).hasSize(100);
        assertThat(listRecord.getResumptionToken()).isNotNull();
        assertThat(listRecord.getResumptionToken().getCursor()).isEqualTo(0);
        assertThat(listRecord.getResumptionToken().getValue()).isEqualTo("YTo1OntzOjQ6InZlcmIiO3M6MTE6Ikxpc3RSZWNvcmRzIjtzOjE0OiJtZXRhZGF0YVByZWZpeCI7czo3OiJvYWlfcmRmIjtzOjM6InNldCI7czo0NjoiYmVuZzpndGFhOjhmY2IxYzRmLTY2M2QtMDBkMy05NWIyLWNjY2Q1YWJkYTM1MiI7czo0OiJmcm9tIjtzOjI1OiIyMDE1LTAxLTMxVDIzOjAwOjAwKzAwOjAwIjtzOjQ6InBhZ2UiO2k6Mjt9");
        assertThat(listRecord.getResumptionToken().getCompleteListSize()).isEqualTo(122486);
        assertThat(listRecord.getRecords().get(0).getMetaData()).isNotNull();
    }
}
