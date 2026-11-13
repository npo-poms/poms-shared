package nl.vpro.media.broadcaster;

import jakarta.inject.Provider;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.domain.user.ServiceLocator;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"removal"})
class BroadcasterServiceLocatorTest {
    private final BroadcasterService broadcasterService = new URLBroadcasterServiceImpl("classpath:/broadcasters.properties", false, true);


    @Test
    void wiredByInject() throws IllegalAccessException {
        BroadcasterServiceLocator locator = new BroadcasterServiceLocator();
        FieldUtils.writeField(locator, "broadcasterServiceProvider", (Provider<BroadcasterService>) () -> broadcasterService, true);
        locator.setSingleton();

        assertThat(BroadcasterServiceLocator.getInstance().find("VPRO")).isEqualTo(new Broadcaster("VPRO"));
        // there is only one broadcaster service
        assertThat(ServiceLocator.getBroadcasterService()).isSameAs(broadcasterService);
        assertThat(BroadcasterServiceLocator.getInstance()).isSameAs(broadcasterService);
        assertThat(BroadcasterServiceLocator.getIds()).containsExactlyInAnyOrder("PP",
    "MTNL",
    "RVD",
    "POGO",
    "BNN",
    "RTOO",
    "TELE",
    "ZVK",
    "RVU",
    "OHM",
    "VRT",
    "RNW",
    "NCRV",
    "LLNK",
    "MO",
    "IKON",
    "RTNO",
    "BOS",
    "EO",
    "AVRO",
    "VARA",
    "RKK",
    "NOS",
    "POWN",
    "NED1",
    "NED2",
    "OBBT",
    "NED3",
    "HUMA",
    "JO",
    "TROS",
    "MAX",
    "RAD5",
    "RTUT",
    "RAD6",
    "KRO",
    "ZAPL",
    "NPO",
    "NTR",
    "ZAPP",
    "NPS",
    "WNL",
    "RTNH",
    "RTRM",
    "VPRO",
    "L1",
    "SOCU",
    "OFVL",
    "OWST",
    "AVTR",
    "OZEE",
    "ROFR",
    "NMO",
    "NIO",
    "RTDR",
    "RAD3",
    "FUNX",
    "RAD4",
    "RAD1",
    "RAD2",
    "BVN",
    "ROGE",
    "KRNC");
         assertThat(BroadcasterServiceLocator.getDisplayNames()).containsExactlyInAnyOrder("Politieke partijen",
    "MTNL",
    "Rijksvoorlichtingsdienst",
    "Polygoon",
    "BNN",
    "RTV Oost",
    "Teleac",
    "ZvK",
    "RVU",
    "OHM",
    "VRT",
    "Radio Nederland Wereldomroep",
    "NCRV",
    "LLiNK",
    "Moslim Omroep",
    "IKON",
    "RTV Noord",
    "BOS",
    "EO",
    "AVRO",
    "VARA",
    "RKK",
    "NOS",
    "PowNed",
    "Nederland 1",
    "Nederland 2",
    "Omroep Brabant",
    "Nederland 3",
    "HUMAN",
    "Joodse Omroep",
    "TROS",
    "MAX",
    "Radio 5",
    "RTV Utrecht",
    "Radio 6",
    "KRO",
    "Zappelin",
    "NPO",
    "NTR",
    "Zapp",
    "NPS",
    "WNL",
    "RTV Noord Holland",
    "RTV Rijnmond",
    "VPRO",
    "L1",
    "Socutera",
    "Omroep Flevoland",
    "Omroep West",
    "AVROTROS",
    "Omroep Zeeland",
    "Omrop Fryslân",
    "NMO",
    "NIO",
    "RTV Drenthe",
    "3FM",
    "FunX",
    "Radio 4",
    "Radio 1",
    "Radio 2",
    "BVN",
    "TV Gelderland",
    "KRO-NCRV");

    }


    @Test
    void setInstance() {
        BroadcasterServiceLocator.setInstance(broadcasterService);
        assertThat(ServiceLocator.findBroadcasterService()).containsSame(broadcasterService);

        BroadcasterServiceLocator.setInstance(null);
        assertThat(ServiceLocator.findBroadcasterService()).isEmpty();
        assertThat(ServiceLocator.getBroadcasterService().findAll()).isEmpty();
    }

    @Test
    void offlineInstanceIsRegistered() {
        ServiceLocator.setBroadcasterService((BroadcasterService) null);
        assertThat(BroadcasterServiceLocator.getOfflineInstance(false, true)).isSameAs(CSVBroadcasterService.CLASSPATH);
        assertThat(ServiceLocator.getBroadcasterService()).isSameAs(CSVBroadcasterService.CLASSPATH);

        // a configured service takes precedence
        ServiceLocator.setBroadcasterService(broadcasterService);
        assertThat(BroadcasterServiceLocator.getOfflineInstance(false, true)).isSameAs(broadcasterService);
        ServiceLocator.setBroadcasterService((BroadcasterService) null);
    }

}
