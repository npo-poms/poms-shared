package nl.vpro.media.tva.bindinc;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.Channel;

import static nl.vpro.media.tva.bindinc.Utils.HEADER_CHANNEL;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 */
@Slf4j
class UtilsTest {

    @Test
    void parseFileName() {
        assertThat(Utils.parseFileName((String) null)).isEmpty();

        Optional<Utils.BindincFile> bindincFile = Utils.parseFileName("1.xml");
        assertThat(bindincFile).isEmpty();

        bindincFile = Utils.parseFileName("20210102050510000dayARTT20210113.xml");
        assertThat(bindincFile).isNotEmpty();


        assertThat(bindincFile.get().getChannel()).isEqualTo(Channel.ART_);
        assertThat(bindincFile.get().getDay()).isEqualTo("2021-01-13");
        assertThat(bindincFile.get().getTimestamp()).isEqualTo("2021-01-02T05:05:10");

        Exchange ex = getExchange();
        Utils.parseFileName(ex);
        assertThat(ex.getIn().getHeader(HEADER_CHANNEL)).isEqualTo(Channel.NED1);
    }


    @Test
    void converters() throws IOException {

        Exchange ex = getExchange();
        Utils.parseFileName(ex);
        assertThat(ex.getIn().getHeader(Exchange.CORRELATION_ID)).isEqualTo("NED1/2020-12-09");

    }


    Exchange getExchange() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader(Exchange.FILE_NAME, "20201208185718000dayTV0120201209.xml");
        ex.getIn().setBody(getClass().getResourceAsStream("/bindinc/20201208185718000dayTV0120201209.xml"));
        return ex;
    }


}
