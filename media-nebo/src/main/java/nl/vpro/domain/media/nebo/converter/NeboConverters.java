package nl.vpro.domain.media.nebo.converter;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;

import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.nebo.enrichment.v2_4.NeboXmlImport;
import nl.vpro.domain.media.nebo.webonly.v1_4.NeboXmlWebOnly;
import nl.vpro.domain.media.update.MediaUpdate;
import nl.vpro.domain.media.update.ProgramUpdate;


/**
 * @author Michiel Meeuwissen
 * @since 1.4
 */
public class NeboConverters {

    public static final String SRID_HEADER = "srid";

    @Converter
    public MediaUpdate<?> convert(NeboXmlImport enrichment) {
        Program program = enrichment.getAflevering().getProgram();
        ProgramUpdate update = ProgramUpdate.create(program);
        // This legacy Nebo format has no series support

        return update;
    }

    @Converter
    public MediaUpdate<?> convert(NeboXmlWebOnly webonly, Exchange exchange) {
        exchange.getIn().setHeader(SRID_HEADER, webonly.getWebonly().getSrid());
        return ProgramUpdate.create(webonly.getProgram());
    }
}
