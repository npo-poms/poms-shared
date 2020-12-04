package nl.vpro.media.tva.saxon.extension;

import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.StringValue;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.NotFoundException;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Michiel Meeuwissen
 * @since 4.5
 */
public class FindBroadcasterFunctionTest {


    @Test
    public void callExpression() throws XPathException, NotFoundException {
        BroadcasterService broadcasterService = mock(BroadcasterService.class);
        FindBroadcasterFunction function = new FindBroadcasterFunction(broadcasterService);

        when(broadcasterService.findForIds(anyString())).thenCallRealMethod();

        when(broadcasterService.findForWhatsOnId("WO")).thenReturn(Broadcaster.of("WOOK"));
        when(broadcasterService.findForWhatsOnId(not(eq("WO")))).thenThrow(new NotFoundException(null, null));
        when(broadcasterService.findForMisId("MIS")).thenReturn(Broadcaster.of("MISOK"));
        when(broadcasterService.findForMisId(not(eq("MIS")))).thenThrow(new NotFoundException(null, null));
        when(broadcasterService.findForNeboId("NEBO")).thenReturn(Broadcaster.of("NEBOOK"));
        when(broadcasterService.findForNeboId(not(eq("NEBO")))).thenThrow(new NotFoundException(null, null));

        when(broadcasterService.find("BLA")).thenReturn(Broadcaster.of("BLA"));



        assertThat(((StringValue) function.makeCallExpression().call(null, new Sequence[]{new StringValue("KRO-NCRV")})).getPrimitiveStringValue()).isEqualTo("KRO-NCRV");
        assertThat(((StringValue) function.makeCallExpression().call(null, new Sequence[]{new StringValue("WO")})).getPrimitiveStringValue()).isEqualTo("WOOK");
        assertThat(((StringValue) function.makeCallExpression().call(null, new Sequence[]{new StringValue("MIS")})).getPrimitiveStringValue()).isEqualTo("MISOK");
        assertThat(((StringValue) function.makeCallExpression().call(null, new Sequence[]{new StringValue("NEBO")})).getPrimitiveStringValue()).isEqualTo("NEBOOK");
        assertThat(((StringValue) function.makeCallExpression().call(null, new Sequence[]{new StringValue("BLA")})).getPrimitiveStringValue()).isEqualTo("BLA");

    }



}
