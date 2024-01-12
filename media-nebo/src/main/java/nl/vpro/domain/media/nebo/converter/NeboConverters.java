package nl.vpro.domain.media.nebo.converter;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;

import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.nebo.enrichment.v2_4.NeboXmlImport;
import nl.vpro.domain.media.nebo.webonly.v1_4.NeboXmlWebOnly;
import nl.vpro.domain.media.update.MediaUpdate;
import nl.vpro.domain.media.update.ProgramUpdate;
import nl.vpro.util.Version;


/**
 * @author Michiel Meeuwissen
 * @since 1.4
 */
@Deprecated
public class NeboConverters {

    public static final String SRID_HEADER = "srid";

    @Converter
    public MediaUpdate<?> convert(NeboXmlImport enrichment, Exchange exchange) {
        Program program = enrichment.getAflevering().getProgram();
        ProgramUpdate update = ProgramUpdate.create(Version.of(4, 0), program);
        // This legacy Nebo format has no series support

        String bc = exchange.getIn().getHeader("broadcaster", String.class);
        update.setBroadcasters(bc);
        return update;
    }

    @Converter
    public MediaUpdate<?> convert(NeboXmlWebOnly webonly, Exchange exchange) {
        exchange.getIn().setHeader(SRID_HEADER, webonly.getWebonly().getSrid());
        ProgramUpdate update  = ProgramUpdate.create(Version.of(4, 0), webonly.getProgram());
        String bc = exchange.getIn().getHeader("broadcaster", String.class);
        update.setBroadcasters(bc);
        return update;
    }
}
