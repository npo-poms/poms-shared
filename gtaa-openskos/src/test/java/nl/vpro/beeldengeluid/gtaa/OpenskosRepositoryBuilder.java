package nl.vpro.beeldengeluid.gtaa;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * This class is duplicated in media-gtaa test
 */
public class OpenskosRepositoryBuilder {

    public static OpenskosRepository getRealInstance(String env) {
        MarshallingHttpMessageConverter marshallingHttpMessageConverter = new MarshallingHttpMessageConverter();
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setPackagesToScan("nl.vpro.beeldengeluid.gtaa", "nl.vpro.w3.rdf", "nl.vpro.openarchives.oai");

        try {
            jaxb2Marshaller.afterPropertiesSet();
        } catch (Exception ex) {
            /* Ignore */
        }
        marshallingHttpMessageConverter.setMarshaller(jaxb2Marshaller);
        marshallingHttpMessageConverter.setUnmarshaller(jaxb2Marshaller);

        RestTemplate template = new RestTemplate();
        template.setMessageConverters(Collections.singletonList(marshallingHttpMessageConverter));

        OpenskosRepository impl;

        // acceptatie
        if ("acceptatie".equals(env)) {
            impl = new OpenskosRepository("http://accept.openskos.beeldengeluid.nl.pictura-dp.nl/", "***REMOVED***", template);
            impl.setPersonsSpec("beng:gtaa:8fcb1c4f-663d-00d3-95b2-cccd5abda352");
        } else if ("test".equals(env)) {
            // test
            impl = new OpenskosRepository("http://beg-openskos.test7.picturae.pro", "***REMOVED***", template);
            impl.setPersonsSpec("beng:gtaa:138d0e62-d688-e289-f136-05ad7acc85a2");
        } else if ("productie".equals(env)) {
            // productie
            impl = new OpenskosRepository("http://openskos.beeldengeluid.nl/", "***REMOVED***", template);
            impl.setPersonsSpec("beng:gtaa:8fcb1c4f-663d-00d3-95b2-cccd5abda352");
        } else {
            // dev
            impl = new OpenskosRepository("http://beg-openskos.test7.picturae.pro/", "***REMOVED***", template);
            impl.setPersonsSpec("beng:gtaa:138d0e62-d688-e289-f136-05ad7acc85a2");
        }
        impl.init();
        impl.setUseXLLabels(true);
        impl.setTenant("beng");

        return impl;
    }
}
