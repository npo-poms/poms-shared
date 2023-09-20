/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.media.odi.util.LocationResult;

import static org.mockito.Mockito.*;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
public class OdiServiceImplTest {

    private Program program;

    private final LocationProducer handlerMock = mock(LocationProducer.class);

    private MockHttpServletRequest request = new MockHttpServletRequest("GET", "somepath");
    private OdiServiceImpl target;


    @BeforeEach
    public void setUp() {
        program = MediaTestDataBuilder.program().locations(
            new Location("odip+http://odi.omroep.nl/video/adaptive/EO_101197072", OwnerType.BROADCASTER),
            new Location("odi+http://odi.omroep.nl/video/wvc1_std/EO_101197072", OwnerType.BROADCASTER, new AVAttributes(1000000, AVFileFormat.WVC1)),
            new Location("odi+http://odi.omroep.nl/video/wmv_bb/EO_101197072", OwnerType.BROADCASTER, new AVAttributes(800000, AVFileFormat.WM)),
            new Location("odi+http://odi.omroep.nl/video/wmv_sb/EO_101197072", OwnerType.BROADCASTER, new AVAttributes(500000, AVFileFormat.WM)),
            new Location("odi+http://odi.omroep.nl/video/h264_std/EO_101197072", OwnerType.BROADCASTER, new AVAttributes(1000000, AVFileFormat.H264)),
            new Location("odi+http://odi.omroep.nl/video/h264_bb/EO_101197072", OwnerType.BROADCASTER, new AVAttributes(800000, AVFileFormat.H264)),
            new Location("odi+http://odi.omroep.nl/video/h264_sb/EO_101197072", OwnerType.BROADCASTER, new AVAttributes(500000, AVFileFormat.H264))
        ).build();


        LocationResult result = mock(LocationResult.class);
        when(handlerMock.produce(any(Location.class), any(HttpServletRequest.class), any(String[].class))).thenReturn(result);
        when(handlerMock.score(any(Location.class), any(String[].class))).thenReturn(1);
        when(handlerMock.produceIfSupports(any(Location.class), any(HttpServletRequest.class), any(String[].class))).thenCallRealMethod();
        when(handlerMock.supports(any(Integer.class))).thenCallRealMethod();


        target = new OdiServiceImpl();
        target.setHandlers(Arrays.asList(handlerMock));
    }

    @Test
    public void testPlayMediaOnDefault() {
        target.playMedia(program, request);

        verify(handlerMock).produce(eq(new Location("odip+http://odi.omroep.nl/video/adaptive/EO_101197072", OwnerType.BROADCASTER)), eq(request));
    }

    @Test
    public void testPlayMediaOnOutput() {
        target.playMedia(program, request);

        verify(handlerMock).produceIfSupports(eq(new Location("odip+http://odi.omroep.nl/video/adaptive/EO_101197072", OwnerType.BROADCASTER)), eq(request));
    }

    @Test
    public void testPlayMediaOnAVFileFormat() {
        target.playMedia(program, request, "H264", "HASP");

        verify(handlerMock).produceIfSupports(eq(new Location("odip+http://odi.omroep.nl/video/adaptive/EO_101197072", OwnerType.BROADCASTER)), eq(request), eq("H264"), eq("HASP"));
        verify(handlerMock).produceIfSupports(eq(new Location("odi+http://odi.omroep.nl/video/h264_std/EO_101197072", OwnerType.BROADCASTER)), eq(request), eq("H264"), eq("HASP"));
    }

    @Test
    public void testPlayMediaOnPubOptions() {
        target.playMedia(program, request, "h264_sb", "h264_bb");

        verify(handlerMock).produceIfSupports(eq(new Location("odi+http://odi.omroep.nl/video/h264_sb/EO_101197072", OwnerType.BROADCASTER)), eq(request), eq("h264_sb"), eq("h264_bb"));
    }

    @Test
    public void testPlayLocation() {

        target.playLocation(program.getPresentationOrderLocations().first(), request);

        verify(handlerMock).produceIfSupports(eq(new Location("odip+http://odi.omroep.nl/video/adaptive/EO_101197072", OwnerType.BROADCASTER)), eq(request));
    }

    @Test
    public void testPlayUrl() {
        target.playUrl("odip+http://odi.omroep.nl/video/adaptive/EO_101197072", request);

        verify(handlerMock).produceIfSupports(eq(new Location("odip+http://odi.omroep.nl/video/adaptive/EO_101197072", OwnerType.BROADCASTER)), eq(request));
    }
}
