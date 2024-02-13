package nl.vpro.domain.media;

import lombok.Getter;

import java.time.Year;
import java.util.*;

import jakarta.xml.bind.annotation.*;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Range;

import nl.vpro.domain.XmlValued;
import nl.vpro.i18n.Displayable;
import nl.vpro.jackson2.BackwardsCompatibleJsonEnum;

import static nl.vpro.domain.media.AVType.AUDIO;
import static nl.vpro.domain.media.AVType.VIDEO;

@XmlEnum
@XmlType(name = "channelEnum")
@JsonSerialize(using = BackwardsCompatibleJsonEnum.Serializer.class)
@JsonDeserialize(using = Channel.Deserializer.class)
public enum Channel implements Displayable, XmlValued {
    @XmlEnumValue("NED1")
    NED1(VIDEO, "https://www.npostart.nl/live/npo-1", 1951) {
        @Override
        public String toString() {
            return "NPO 1";
        }

        @Override
        public String misId() {
            return "TV01";
        }

        @Override
        public String pdId() {
            return "NED1";
        }
    },


    @XmlEnumValue("NED2")
    NED2(VIDEO,  "https://www.npostart.nl/live/npo-2", 1964) {
        @Override
        public String toString() {
            return "NPO 2";
        }

        @Override
        public String misId() {
            return "TV02";
        }

        @Override
        public String pdId() {
            return "NED2";
        }
    },

    @XmlEnumValue("NED3")
    NED3(VIDEO, 1988) {
        @Override
        public String toString() {
            return "NPO 3";
        }

        @Override
        public String misId() {
            return "TV03";
        }

        @Override
        public String pdId() {
            return "NED3";
        }
    },

    @XmlEnumValue("RAD1")
    RAD1(AUDIO,   "https://www.nporadio1.nl/", 1947) { // voorheen Hilversum 2
        @Override
        public String toString() {
            return "Radio 1";
        }

        @Override
        public String pdId() {
            return "RAD1";
        }
    },

    @XmlEnumValue("RAD2")
    RAD2(AUDIO, "https://www.nporadio2.nl/", 1947) { // voorheen Hilversum 1
        @Override
        public String toString() {
            return "Radio 2";
        }

        @Override
        public String pdId() {
            return "RAD2";
        }
    },

    @XmlEnumValue("R2SJ")
    SENJ(AUDIO, Range.atLeast(Year.of(2014)), "https://www.nporadio2.nl/soulenjazz"   , true) { // voorheen radio 6?
        @Override
        public String toString() {
            return "Radio 2 Soul & Jazz";
        }
    },

    @XmlEnumValue("RAD3")
    RAD3(AUDIO,  "https://www.npo3fm.nl/", 1965) {
        @Override
        public String toString() {
            return "3FM";
        }

        @Override
        public String pdId() {
            return "RAD3";
        }
    },



    @XmlEnumValue("R3AL")
    R3AL(AUDIO, Range.atMost(Year.of(2022)), "https://www.npo3fm.nl/kx/programmas/kx-alternative", true) {
        @Override
        public String toString() {
            return "NPO 3FM KX Alternative";
        }
    },

    @XmlEnumValue("R3KX")
    KXFM(AUDIO, Range.atLeast(Year.of(2005)), "https://www.npo3fm.nl/kx", true) {
        @Override
        public String toString() {
            return "NPO 3FM KX Radio";
        }
    },

    @XmlEnumValue("RAD4")
    RAD4(AUDIO, 1975) {
        @Override
        public String toString() {
            return "Radio 4";
        }

        @Override
        public String pdId() {
            return "RAD4";
        }
    },

    @XmlEnumValue("R4CO")
    R4CO(AUDIO, null, null, true) {
        @Override
        public String toString() {
            return "NPO Radio 4 Concerten";
        }
    },

    @XmlEnumValue("RAD5")
    RAD5(AUDIO, 1983) {
        @Override
        public String toString() {
            return "Radio 5";
        }

    },

    @XmlEnumValue("R5ST")
    STNL(AUDIO, Range.atLeast(Year.of(2010)), "https://www.nporadio5.nl/sterrennl/online-radio-luisteren/", true) {
        @Override
        public String toString() {
            return "NPO Radio 5 Sterren NL";
        }
    },

    @XmlEnumValue("RAD6")
    RAD6(AUDIO, 2006, 2016) {
        @Override
        public String toString() {
            return "Radio 6";
        }

        @Override
        public String pdId() {
            return "RAD6";
        }
    },

    @XmlEnumValue("RTL4")
    RTL4(VIDEO, 1989) {
        @Override
        public String toString() {
            return "RTL 4";
        }
    },

    @XmlEnumValue("RTL5")
    RTL5(VIDEO, 1993) {
        @Override
        public String toString() {
            return "RTL 5";
        }
    },

    @XmlEnumValue("SBS6")
    SBS6(VIDEO, 1995) {
        @Override
        public String toString() {
            return "SBS 6";
        }
    },

    @XmlEnumValue("RTL7")
    RTL7(VIDEO, 2005) {
        @Override
        public String toString() {
            return "RTL 7";
        }
    },

    @XmlEnumValue("VERO")
    VERO(VIDEO) {
        @Override
        public String toString() {
            return "Veronica/Jetix";
        }
    },

    @XmlEnumValue("NET5")
    NET5(VIDEO, 1999) {
        @Override
        public String toString() {
            return "Net 5";
        }
    },

    @XmlEnumValue("RTL8")
    RTL8(VIDEO, 2007) {
        @Override
        public String toString() {
            return "RTL 8";
        }
    },

    @XmlEnumValue("REGI")
    REGI {
        @Override
        public String toString() {
            return "Regionale TV combikanaal";
        }
    },

    @XmlEnumValue("OFRY")
    OFRY {
        @Override
        public String toString() {
            return "Omrop Fryslân";
        }

        @Override
        public String pdId() {
            return "OFRY";
        }
    },

    @XmlEnumValue("NOOR")
    NOOR {
        @Override
        public String toString() {
            return "TV Noord";
        }

        @Override
        public String pdId() {
            return "NOOR";
        }
    },

    @XmlEnumValue("RTVD")
    RTVD {
        @Override
        public String toString() {
            return "RTV Drenthe";
        }

        @Override
        public String pdId() {
            return "RTVD";
        }
    },


    @XmlEnumValue("OOST")
    OOST {
        @Override
        public String toString() {
            return "TV Oost";
        }

        @Override
        public String pdId() {
            return "OOST";
        }
    },

    @XmlEnumValue("GELD")
    GELD {
        @Override
        public String toString() {
            return "TV Gelderland";
        }

        @Override
        public String pdId() {
            return "GELD";
        }
    },

    @XmlEnumValue("FLEV")
    FLEV {
        @Override
        public String toString() {
            return "TV Flevoland";
        }

        @Override
        public String pdId() {
            return "FLEV";
        }
    },

    @XmlEnumValue("BRAB")
    BRAB {
        @Override
        public String toString() {
            return "Omroep Brabant";
        }

        @Override
        public String pdId() {
            return "BRAB";
        }
    },

    @XmlEnumValue("REGU")
    REGU {
        @Override
        public String toString() {
            return "RTV Utrecht";
        }

        @Override
        public String pdId() {
            return "RTVU";
        }
    },

    @XmlEnumValue("NORH")
    NORH {
        @Override
        public String toString() {
            return "TV Noord-Holland";
        }

        @Override
        public String pdId() {
            return "NORH";
        }
    },

    @XmlEnumValue("WEST")
    WEST {
        @Override
        public String toString() {
            return "TV West";
        }

        @Override
        public String pdId() {
            return "WEST";
        }
    },

    @XmlEnumValue("RIJN")
    RIJN {
        @Override
        public String toString() {
            return "TV Rijnmond";
        }

        @Override
        public String pdId() {
            return "RIJN";
        }
    },

    @XmlEnumValue("L1TV")
    L1TV {
        @Override
        public String toString() {
            return "L1 TV";
        }

        @Override
        public String pdId() {
            return "L1TV";
        }
    },

    @XmlEnumValue("OZEE")
    OZEE {
        @Override
        public String toString() {
            return "Omroep Zeeland";
        }

        @Override
        public String pdId() {
            return "OZEE";
        }
    },
    @XmlEnumValue("TVDR")
    TVDR(VIDEO, "https://www.rtvdrenthe.nl/tv", 1995) {
        @Override
        public String toString() {
            return "TV Drenthe";
        }

        @Override
        public String pdId() {
            return "TVDR";
        }
    },
    @XmlEnumValue("AT5_")
    AT5_(VIDEO, 1992) {
        @Override
        public String toString() {
            return "AT 5";
        }

        @Override
        public String pdId() {
            return "AT5";
        }
    },

    @XmlEnumValue("RNN7")
    RNN7 {
        @Override
        public String toString() {
            return "RNN7";
        }
    },

    @XmlEnumValue("BVNT")
    BVNT {
        @Override
        public String toString() {
            return "BVN-TV";
        }
    },

    @XmlEnumValue("EEN_")
    EEN_(VIDEO, 1953) {
        @Override
        public String toString() {
            return "Eén";
        }
    },

    @XmlEnumValue("KETN")
    KETN(VIDEO, 1997) {
        @Override
        public String toString() {
            return "Ketnet"; // Since 2012 zijn Ketnet en Canvas 2 kanalen.
        }
    },

    @XmlEnumValue("VTM_")
    VTM_ {
        @Override
        public String toString() {
            return "VTM";
        }
    },

    @XmlEnumValue("KA2_")
    KA2_ {
        @Override
        public String toString() {
            return "KANAALTWEE";
        }
    },

    @XmlEnumValue("VT4_") // Was VIER, VT4
    VT4_(VIDEO, "https://www.goplay.be/programmas/play4", 1995) {
        @Override
        public String toString() {
            return "Play4";
        }
    },

    @XmlEnumValue("LUNE")
    LUNE {
        @Override
        public String toString() {
            return "La Une (RTBF 1)";
        }
    },

    @XmlEnumValue("LDUE")
    LDUE {
        @Override
        public String toString() {
            return "La Deux (RTBF 2)";
        }
    },

    @XmlEnumValue("RTBF")
    RTBF {
        @Override
        public String toString() {
            return "RTBF Sat";
        }
    },

    @XmlEnumValue("ARD_")
    ARD_(VIDEO, 1952) {
        @Override
        public String toString() {
            return "ARD";
        }
    },

    @XmlEnumValue("ZDF_")
    ZDF_(VIDEO, 1963) {
        @Override
        public String toString() {
            return "ZDF";
        }
    },

    @XmlEnumValue("WDR_")
    WDR_(VIDEO, 1956) {
        @Override
        public String toString() {
            return "WDR Fernsehen";
        }
    },

    /**
     */
    @XmlEnumValue("N_3_")
    N_3_(AUDIO) {
        @Override
        public String toString() {
            return "NDR Kultur";
        }
    },

    @XmlEnumValue("SUDW")
    SUDW {
        @Override
        public String toString() {
            return "SWF Baden-Württemberg";

        }
    },

    @XmlEnumValue("SWF_")
    SWF_ {
        @Override
        public String toString() {
            return "SWF Rheinland-Pfalz";
        }
    },

    @XmlEnumValue("RTL_")
    RTL_ {
        @Override
        public String toString() {
            return "RTL Television";
        }
    },

    @XmlEnumValue("SAT1")
    SAT1 {
        @Override
        public String toString() {
            return "Sat1";
        }
    },

    @XmlEnumValue("PRO7")
    PRO7 {
        @Override
        public String toString() {
            return "Pro7";
        }
    },

    @XmlEnumValue("3SAT")
    _3SAT {
        @Override
        public String toString() {
            return "3 Sat";
        }
    },

    @XmlEnumValue("KABE")
    KABE {
        @Override
        public String toString() {
            return "Kabel 1";
        }
    },


    @XmlEnumValue("ARTE")
    ARTE {
        @Override
        public String toString() {
            return "ARTE France";
        }
    },

    @XmlEnumValue("ART")
    ART_ {
        @Override
        public String toString() {
            return "ARTE Deutschland";
        }
    },


    @XmlEnumValue("T5ME")
    T5ME {
        @Override
        public String toString() {
            return "TV 5 Monde Europe";
        }
    },


    @XmlEnumValue("FRA2")
    FRA2 {
        @Override
        public String toString() {
            return "France 2";
        }
    },

    @XmlEnumValue("FRA3")
    FRA3 {
        @Override
        public String toString() {
            return "France 3";
        }
    },

    @XmlEnumValue("BBC1")
    BBC1 {
        @Override
        public String toString() {
            return "BBC 1";
        }
    },

    @XmlEnumValue("BBC2")
    BBC2 {
        @Override
        public String toString() {
            return "BBC 2";
        }
    },



    @XmlEnumValue("BBTH")
    BBTH {
        @Override
        public String toString() {
            return "BBC Three";
        }
    },

    @XmlEnumValue("BBTC")
    BBTC(VIDEO, 1985) {
        @Override
        public String toString() {
            return "BBC Three / CBBC";
        }
    },

    @XmlEnumValue("BBCF")
    BBCF {
        @Override
        public String toString() {
            return "BBC Four";
        }
    },

    @XmlEnumValue("BBFC")
    BBFC {
        @Override
        public String toString() {
            return "BBC Four / Ceebies";
        }
    },

    @XmlEnumValue("BBCP")
    BBCP {
        @Override
        public String toString() {
            return "BBC Prime";
        }
    },

    @XmlEnumValue("TRTI")
    TRTI(VIDEO, 1990, 2009) {
        @Override
        public String toString() {
            return "TRT International";
        }
    },

    /**
     * @since 5.30
     */
    @XmlEnumValue("TRT1")
    TRT1(VIDEO, "https://www.trt1.com.tr/", 1968){
        @Override
        public String toString() {
            return "TRT 1";
        }
    },

    @XmlEnumValue("SHOW")
    SHOW {
        @Override
        public String toString() {
            return "ShowTV";
        }
    },

    @XmlEnumValue("LIGT")
    LIGT {
        @Override
        public String toString() {
            return "LigTV";
        }
    },

    @XmlEnumValue("TURK")
    TURK {
        @Override
        public String toString() {
            return "Turkmax";
        }
    },
    @XmlEnumValue("ATVT")
    ATVT {
        @Override
        public String toString() {
            return "ATV";
        }
    },

    @XmlEnumValue("RRTM")
    RRTM {
        @Override
        public String toString() {
            return "RTM";
        }
    },

    @XmlEnumValue("RMBC")
    RMBC {
        @Override
        public String toString() {
            return "MBC";
        }
    },

    @XmlEnumValue("RART")
    RART {
        @Override
        public String toString() {
            return "ART Europe";
        }
    },

    @XmlEnumValue("ARTM")
    ARTM {
        @Override
        public String toString() {
            return "ART Movie";
        }
    },

    @XmlEnumValue("TVBS")
    TVBS {
        @Override
        public String toString() {
            return "TVBS Europe";
        }
    },

    @XmlEnumValue("ASIA")
    ASIA {
        @Override
        public String toString() {
            return "Sony Ent TV Asia";
        }
    },

    @XmlEnumValue("TIVI")
    TIVI {
        @Override
        public String toString() {
            return "A-Tivi";
        }
    },

    @XmlEnumValue("B4UM")
    B4UM {
        @Override
        public String toString() {
            return "B4U Movies";
        }
    },

    @XmlEnumValue("PCNE")
    PCNE {
        @Override
        public String toString() {
            return "Phoenix CNE";
        }
    },

    @XmlEnumValue("PATN")
    PATN {
        @Override
        public String toString() {
            return "ATN";
        }
    },

    @XmlEnumValue("ZEET")
    ZEET {
        @Override
        public String toString() {
            return "Zee TV";
        }
    },

    @XmlEnumValue("ZEEC")
    ZEEC {
        @Override
        public String toString() {
            return "Zee Cinema";
        }
    },

    @XmlEnumValue("TVE_")
    TVE_ {
        @Override
        public String toString() {
            return "TVE";
        }
    },

    @XmlEnumValue("RAI_")
    RAI_ {
        @Override
        public String toString() {
            return "Rai Uno";
        }
    },

    @XmlEnumValue("RAID")
    RAID {
        @Override
        public String toString() {
            return "Rai Due";
        }
    },

    @XmlEnumValue("RAIT")
    RAIT {
        @Override
        public String toString() {
            return "Rai Tre";
        }
    },

    @XmlEnumValue("TEVE")
    TEVE {
        @Override
        public String toString() {
            return "TeVe Sur";
        }
    },

    @XmlEnumValue("ERTS")
    ERTS {
        @Override
        public String toString() {
            return "ERT Sat";
        }
    },

    @XmlEnumValue("STV_")
    STV_ {
        @Override
        public String toString() {
            return "STV";
        }
    },

    @XmlEnumValue("NTV_")
    NTV_ {
        @Override
        public String toString() {
            return "NTV";
        }
    },

    @XmlEnumValue("NOSJ")
    NOSJ {
        @Override
        public String toString() {
            return "NPO Nieuws";
        }

        @Override
        public String pdId() {
            return "NOSJ";
        }
    },

    @XmlEnumValue("CULT")
    CULT {
        @Override
        public String toString() {
            return "NPO 2 extra";
        }

        @Override
        public String pdId() {
            return "CULT";
        }
    },

    @XmlEnumValue("101_")
    _101_ {
        @Override
        public String toString() {
            return "NPO 101";
        }

    },

    @XmlEnumValue("PO24")
    PO24 {
        @Override
        public String toString() {
            return "NPO Politiek";
        }

        @Override
        public String pdId() {
            return "PO24";
        }
    },

    @XmlEnumValue("HILV")
    HILV {
        @Override
        public String toString() {
            return "NPO Best";
        }

        @Override
        public String pdId() {
            return "HILV";
        }
    },

    @XmlEnumValue("HOLL")
    HOLL {
        @Override
        public String toString() {
            return "NPO Doc";
        }

        @Override
        public String pdId() {
            return "HOLL";
        }
    },

    @XmlEnumValue("GESC")
    GESC {
        @Override
        public String toString() {
            return "/Geschiedenis";
        }
    },

    @XmlEnumValue("3VCN")
    _3VCN {
        @Override
        public String toString() {
            return "3voor12 Central";
        }
    },

    @XmlEnumValue("3VOS")
    _3VOS {
        @Override
        public String toString() {
            return "3voor12 On stage";
        }
    },

    @XmlEnumValue("NEDE")
    NEDE {
        @Override
        public String toString() {
            return "Nederland-e";
        }
    },

    @XmlEnumValue("STER")
    STER {
        @Override
        public String toString() {
            return "Sterren.nl";
        }
    },

    @XmlEnumValue("OMEG")
    OMEG {
        @Override
        public String toString() {
            return "Omega TV";
        }
    },

    @XmlEnumValue("NCRV")
    NCRV {
        @Override
        public String toString() {
            return "NCRV /Geloven";
        }
    },

    @XmlEnumValue("OPVO")
    OPVO {
        @Override
        public String toString() {
            return "NPO Zapp Xtra / NPO Zappelin Xtra";
        }

        @Override
        public String pdId() {
            return "OPVO";
        }
    },

    @XmlEnumValue("CONS")
    CONS {
        @Override
        public String toString() {
            return "Consumenten TV";
        }
    },

    @XmlEnumValue("HUMO")
    HUMO {
        @Override
        public String toString() {
            return "NPO Humor TV";
        }

        @Override
        public String pdId() {
            return "HUMO";
        }
    },

    @XmlEnumValue("DIER")
    DIER {
        @Override
        public String toString() {
            return "AVRO Dier en Natuur";
        }
    },

    @XmlEnumValue("ENTE")
    ENTE {
        @Override
        public String toString() {
            return "E! Entertainment";
        }
    },

    @XmlEnumValue("FASH")
    FASH {
        @Override
        public String toString() {
            return "Fashion TV";
        }
    },

    @XmlEnumValue("COMC")
    COMC {
        @Override
        public String toString() {
            return "Comedy CentralNickelodeon";
        }
    },
    @XmlEnumValue("COMF")
    COMF {
        @Override
        public String toString() {
            return "Comedy Family";
        }
    },

    @XmlEnumValue("TBN_")
    TBN_ {
        @Override
        public String toString() {
            return "TBN Europe";
        }
    },

    @XmlEnumValue("DISC")
    DISC {
        @Override
        public String toString() {
            return "Discovery Channel";
        }
    },

    @XmlEnumValue("ZONE")
    ZONE {
        @Override
        public String toString() {
            return "Zone Reality (UK)";
        }
    },

    @XmlEnumValue("ANPL")
    ANPL {
        @Override
        public String toString() {
            return "Animal Planet";
        }
    },

    @XmlEnumValue("CLUB")
    CLUB {
        @Override
        public String toString() {
            return "Zone Club";
        }
    },

    @XmlEnumValue("NAGE")
    NAGE {
        @Override
        public String toString() {
            return "National Geographic/CNBC";
        }
    },

    @XmlEnumValue("TRAC")
    TRAC {
        @Override
        public String toString() {
            return "Trace TV";
        }
    },

    @XmlEnumValue("NGHD")
    NGHD {
        @Override
        public String toString() {
            return "National Geographic HD";
        }
    },

    @XmlEnumValue("WILD")
    WILD {
        @Override
        public String toString() {
            return "Nat Geo Wild";
        }
    },

    @XmlEnumValue("GARU")
    GARU {
        @Override
        public String toString() {
            return "Garuda TV";
        }
    },

    @XmlEnumValue("ZAZA")
    ZAZA {
        @Override
        public String toString() {
            return "Zazaro TV";
        }
    },

    @XmlEnumValue("FAM7")
    FAM7 {
        @Override
        public String toString() {
            return "Family7";
        }
    },

    @XmlEnumValue("DTAL")
    DTAL {
        @Override
        public String toString() {
            return "Discovery Travel & Living";
        }
    },

    @XmlEnumValue("SCIE")
    SCIE {
        @Override
        public String toString() {
            return "Discovery Science";
        }
    },

    @XmlEnumValue("CIVI")
    CIVI {
        @Override
        public String toString() {
            return "Discovery Civilisation";
        }
    },

    @XmlEnumValue("DIHD")
    DIHD {
        @Override
        public String toString() {
            return "Discovery HD";
        }
    },

    @XmlEnumValue("HIST")
    HIST {
        @Override
        public String toString() {
            return "The History Channel";
        }
    },

    @XmlEnumValue("TRAV")
    TRAV {
        @Override
        public String toString() {
            return "Travel Channel";
        }
    },

    @XmlEnumValue("HETG")
    HETG {
        @Override
        public String toString() {
            return "Het Gesprek";
        }
    },

    @XmlEnumValue("GOED")
    GOED {
        @Override
        public String toString() {
            return "GoedTV";
        }
    },

    @XmlEnumValue("BABY")
    BABY {
        @Override
        public String toString() {
            return "Baby TV";
        }
    },

    @XmlEnumValue("DH1_")
    DH1_ {
        @Override
        public String toString() {
            return "HD-NL";
        }
    },

    @XmlEnumValue("LITV")
    LITV {
        @Override
        public String toString() {
            return "Liberty TV";
        }
    },

    @XmlEnumValue("LIVE")
    LIVE {
        @Override
        public String toString() {
            return "Liveshop";
        }
    },

    @XmlEnumValue("STAR")
    STAR {
        @Override
        public String toString() {
            return "Star!";
        }
    },

    @XmlEnumValue("WEER")
    WEER {
        @Override
        public String toString() {
            return "Weerkanaal";
        }
    },

    @XmlEnumValue("REAL")
    REAL {
        @Override
        public String toString() {
            return "Zone Reality";
        }
    },

    @XmlEnumValue("SCIF")
    SCIF {
        @Override
        public String toString() {
            return "Sci-Fi Channel";
        }
    },

    @XmlEnumValue("13ST")
    _13ST {
        @Override
        public String toString() {
            return "13Th Street";
        }
    },

    @XmlEnumValue("CARC")
    CARC {
        @Override
        public String toString() {
            return "Car Channel";
        }
    },

    @XmlEnumValue("NOSN")
    NOSN {
        @Override
        public String toString() {
            return "ONS"; // Sinds najaar 2015 wordt Nostalgienet 'ONS'.
            //return "NostalgieNet";
        }
    },

    @XmlEnumValue("HISH")
    HISH {
        @Override
        public String toString() {
            return "The History Channel HD";
        }
    },

    @XmlEnumValue("NICK")
    NICK {
        @Override
        public String toString() {
            return "Nickelodeon";
        }
    },

    @XmlEnumValue("NIJN")
    NIJN {
        @Override
        public String toString() {
            return "Nick Jr.";
        }
    },

    @XmlEnumValue("NIKT")
    NIKT {
        @Override
        public String toString() {
            return "Nick Toons";
        }
    },

    @XmlEnumValue("NIKH")
    NIKH {
        @Override
        public String toString() {
            return "Nick Hits";
        }
    },

    @XmlEnumValue("CART")
    CART {
        @Override
        public String toString() {
            return "Cartoon Network";
        }
    },

    @XmlEnumValue("BOOM")
    BOOM {
        @Override
        public String toString() {
            return "Boomerang";
        }
    },

    @XmlEnumValue("CNN_")
    CNN_ {
        @Override
        public String toString() {
            return "CNN";
        }
    },

    @XmlEnumValue("BBCW")
    BBCW {
        @Override
        public String toString() {
            return "BBC World";
        }
    },

    @XmlEnumValue("EURN")
    EURN {
        @Override
        public String toString() {
            return "Euronews";
        }
    },

    @XmlEnumValue("SKNE")
    SKNE {
        @Override
        public String toString() {
            return "Sky News";
        }
    },

    @XmlEnumValue("BLOO")
    BLOO {
        @Override
        public String toString() {
            return "Bloomberg TV";
        }
    },

    @XmlEnumValue("CNBC")
    CNBC {
        @Override
        public String toString() {
            return "CNBC Europe";
        }
    },

    @XmlEnumValue("PALJ")
    PALJ {
        @Override
        public String toString() {
            return "Al Jazeera Arabisch";
        }
    },

    @XmlEnumValue("ALJA")
    ALJA(VIDEO, "https://aljazeera.net", 1996) {
        @Override
        public String toString() {
            return "Al Jazeera";
        }
    },

    @XmlEnumValue("ALJI")
    ALJI(VIDEO, "https://aljazeera.com", 2006) {
        @Override
        public String toString() {
            return "Al Jazeera English";
        }
    },

    @XmlEnumValue("FOXN")
    FOXN {
        @Override
        public String toString() {
            return "Fox News";
        }
    },

    /**
     * @since 5.11
     */
    @XmlEnumValue("FOXT")
    FOXT {
        @Override
        public String toString() {
            return "Fox Türk";
        }
    },

    @XmlEnumValue("MTV_")
    MTV_ {
        @Override
        public String toString() {
            return "MTV";
        }
    },

    @XmlEnumValue("MTV2")
    MTV2 {
        @Override
        public String toString() {
            return "MTV2";
        }
    },

    @XmlEnumValue("HITS")
    HITS {
        @Override
        public String toString() {
            return "MTV Hits";
        }
    },

    @XmlEnumValue("BASE")
    BASE {
        @Override
        public String toString() {
            return "MTV Base";
        }
    },

    @XmlEnumValue("MTVB")
    MTVB {
        @Override
        public String toString() {
            return "MTV Brand New";
        }
    },

    @XmlEnumValue("TMF_")
    TMF_ {
        @Override
        public String toString() {
            return "TMF";
        }
    },

    @XmlEnumValue("TMFN")
    TMFN {
        @Override
        public String toString() {
            return "TMF NL";
        }
    },

    @XmlEnumValue("TMFP")
    TMFP {
        @Override
        public String toString() {
            return "TMF Party";
        }
    },

    @XmlEnumValue("TMFY")
    TMFY {
        @Override
        public String toString() {
            return "TMF Pure";
        }
    },

    @XmlEnumValue("TVOR")
    TVOR {
        @Override
        public String toString() {
            return "TV Oranje";
        }
    },

    @XmlEnumValue("VH1E")
    VH1E {
        @Override
        public String toString() {
            return "VH-1 (EU)";
        }
    },

    @XmlEnumValue("VH1C")
    VH1C {
        @Override
        public String toString() {
            return "VH-1 Classic";
        }
    },

    @XmlEnumValue("PERC")
    PERC {
        @Override
        public String toString() {
            return "Performance Channel";
        }
    },

    @XmlEnumValue("MEZZ")
    MEZZ {
        @Override
        public String toString() {
            return "Mezzo";
        }
    },

    @XmlEnumValue("EURO")
    EURO {
        @Override
        public String toString() {
            return "Eurosport";
        }
    },

    @XmlEnumValue("EUR2")
    EUR2 {
        @Override
        public String toString() {
            return "Eurosport 2";
        }
    },

    @XmlEnumValue("EXTR")
    EXTR {
        @Override
        public String toString() {
            return "Extreme Sports Channel (EU)";
        }
    },

    @XmlEnumValue("MOTO")
    MOTO {
        @Override
        public String toString() {
            return "Motors TV";
        }
    },

    @XmlEnumValue("SAIL")
    SAIL {
        @Override
        public String toString() {
            return "Sailing channel";
        }
    },

    @XmlEnumValue("ESPN")
    ESPN {
        @Override
        public String toString() {
            return "ESPN Classic Sport";
        }
    },

    @XmlEnumValue("NASE")
    NASE {
        @Override
        public String toString() {
            return "NASN Europe";
        }
    },

    @XmlEnumValue("SP11")
    SP11 {
        @Override
        public String toString() {
            return "Sport1.1";
        }
    },

    @XmlEnumValue("SP12")
    SP12 {
        @Override
        public String toString() {
            return "Sport1.2";
        }
    },

    @XmlEnumValue("SP13")
    SP13 {
        @Override
        public String toString() {
            return "Sport1.3";
        }
    },

    @XmlEnumValue("SP14")
    SP14 {
        @Override
        public String toString() {
            return "Sport1.4";
        }
    },

    @XmlEnumValue("SP15")
    SP15 {
        @Override
        public String toString() {
            return "Sport1.5";
        }
    },

    @XmlEnumValue("SP16")
    SP16 {
        @Override
        public String toString() {
            return "Sport1.6";
        }
    },

    @XmlEnumValue("SP17")
    SP17 {
        @Override
        public String toString() {
            return "Sport1.7";
        }
    },

    @XmlEnumValue("SP18")
    SP18 {
        @Override
        public String toString() {
            return "Sport1.8";
        }
    },

    @XmlEnumValue("S1HD")
    S1HD {
        @Override
        public String toString() {
            return "Sport1 HD";
        }
    },

    @XmlEnumValue("FIL1")
    FIL1 {
        @Override
        public String toString() {
            return "Film1.1";
        }
    },

    @XmlEnumValue("FIL2")
    FIL2 {
        @Override
        public String toString() {
            return "Film1.2";
        }
    },

    @XmlEnumValue("FIL3")
    FIL3 {
        @Override
        public String toString() {
            return "Film1.3";
        }
    },

    @XmlEnumValue("FL11")
    FL11 {
        @Override
        public String toString() {
            return "Film1.1 DI";
        }
    },

    @XmlEnumValue("FL1P")
    FL1P {
        @Override
        public String toString() {
            return "Film1+1 DI";
        }
    },

    @XmlEnumValue("FL12")
    FL12 {
        @Override
        public String toString() {
            return "Film1.2 DI";
        }
    },

    @XmlEnumValue("FL13")
    FL13 {
        @Override
        public String toString() {
            return "Film1.3 DI";
        }
    },

    @XmlEnumValue("FLHD")
    FLHD {
        @Override
        public String toString() {
            return "Film1 HD DI";
        }
    },

    @XmlEnumValue("MGMM")
    MGMM {
        @Override
        public String toString() {
            return "MGM Movie Channel";
        }
    },

    @XmlEnumValue("TCM_")
    TCM_ {
        @Override
        public String toString() {
            return "TCM";
        }
    },

    @XmlEnumValue("HALL")
    HALL {
        @Override
        public String toString() {
            return "Hallmark";
        }
    },
    /**
     * @since 5.11
     */
    @XmlEnumValue("HABN")
    HABN {
        @Override
        public String toString() {
            return "HaberTürk";
        }
    },

    @XmlEnumValue("ACNW")
    ACNW {
        @Override
        public String toString() {
            return "Action Now!";
        }
    },

    @XmlEnumValue("RHUS")
    RHUS {
        @Override
        public String toString() {
            return "Hustler TV";
        }
    },

    @XmlEnumValue("PLAY")
    PLAY {
        @Override
        public String toString() {
            return "Playboy TV";
        }
    },

    @XmlEnumValue("ADUL")
    ADUL {
        @Override
        public String toString() {
            return "Adult Channel";
        }
    },

    @XmlEnumValue("PSPI")
    PSPI {
        @Override
        public String toString() {
            return "Private Spice";
        }
    },

    @XmlEnumValue("HUST")
    HUST {
        @Override
        public String toString() {
            return "Blue Hustler";
        }
    },

    @XmlEnumValue("OXMO")
    OXMO {
        @Override
        public String toString() {
            return "XMO";
        }
    },

    @XmlEnumValue("REGR")
    REGR {
        @Override
        public String toString() {
            return "Regionale radio combikanaal";
        }
    },

    @XmlEnumValue("RFRY")
    RFRY {
        @Override
        public String toString() {
            return "R Omrop Fryslan";
        }

        @Override
        public String pdId() {
            return "RFRY";
        }
    },

    /**
     * @since 5.25
     */
    @XmlEnumValue("DRRD")
    DRRD(AVType.AUDIO, "https://www.rtvdrenthe.nl/radio", 1989) {
        @Override
        public String toString() {
            return "Radio Drenthe";
        }

        @Override
        public String pdId() {
            return "DRRD";
        }
    },

    @XmlEnumValue("RNOO")
    RNOO {
        @Override
        public String toString() {
            return "Radio Noord";
        }

        @Override
        public String pdId() {
            return "RNOO";
        }
    },

    @XmlEnumValue("ROST")
    ROST {
        @Override
        public String toString() {
            return "Radio Oost";
        }

        @Override
        public String pdId() {
            return "ROST";
        }
    },

    @XmlEnumValue("RGEL")
    RGEL {
        @Override
        public String toString() {
            return "Radio Gelderland";
        }

        @Override
        public String pdId() {
            return "RGEL";
        }


    },

    @XmlEnumValue("RFLE")
    RFLE {
        @Override
        public String toString() {
            return "Radio Flevoland";
        }

        @Override
        public String pdId() {
            return "RFLE";
        }
    },

    @XmlEnumValue("RBRA")
    RBRA {
        @Override
        public String toString() {
            return "R Omroep Brabant";
        }

        @Override
        public String pdId() {
            return "RBRA";
        }


    },

    @XmlEnumValue("RUTR")
    RUTR {
        @Override
        public String toString() {
            return "Radio M Utrecht";
        }

        @Override
        public String pdId() {
            return "RUTR";
        }
    },

    @XmlEnumValue("RNOH")
    RNOH {
        @Override
        public String toString() {
            return "Radio Noord-Holland";
        }

        @Override
        public String pdId() {
            return "RNOH";
        }
    },

    @XmlEnumValue("RWST")
    RWST {
        @Override
        public String toString() {
            return "89,3 Radio West";
        }

        @Override
        public String pdId() {
            return "RWST";
        }
    },

    @XmlEnumValue("RRIJ")
    RRIJ {
        @Override
        public String toString() {
            return "Radio Rijnmond";
        }

        @Override
        public String pdId() {
            return "RRIJ";
        }
    },

    @XmlEnumValue("LRAD")
    LRAD {
        @Override
        public String toString() {
            return "L1 Radio";
        }

        @Override
        public String pdId() {
            return "LRAD";
        }
    },

    @XmlEnumValue("RZEE")
    RZEE {
        @Override
        public String toString() {
            return "R Omroep Zeeland";
        }

        @Override
        public String pdId() {
            return "RZEE";
        }
    },

    @XmlEnumValue("COMM")
    COMM {
        @Override
        public String toString() {
            return "Commercieelen radio combikanaal";
        }
    },

    @XmlEnumValue("RVER")
    RVER {
        @Override
        public String toString() {
            return "Radio Veronica";
        }
    },

    @XmlEnumValue("SLAM")
    SLAM(AUDIO, "https://www.slam.nl/", 1996) {
        @Override
        public String toString() {
            return "SLAM!";
        }
    },

    @XmlEnumValue("SKYR")
    SKYR(AUDIO, 1988) {
        @Override
        public String toString() {
            return "Sky Radio";
        }
    },

    @XmlEnumValue("RTLF")
    RTLF(AUDIO, 2003, 2006) {
        @Override
        public String toString() {
            return "RTL FM";
        }
    },

    @XmlEnumValue("BNRN")
    BNRN(AUDIO, 1998) {
        @Override
        public String toString() {
            return "BNR Nieuwsradio";
        }
    },

    @XmlEnumValue("KINK")
    KINK(AUDIO, 1995, 2011) {
        @Override
        public String toString() {
            return "Kink FM";
        }
    },

    @XmlEnumValue("PCAZ")
    PCAZ(AUDIO, 2006) {
        @Override
        public String toString() {
            return "Arrow Caz!";
        }
    },

    @XmlEnumValue("QMUS")
    QMUS(AUDIO, 2005) {
        @Override
        public String toString() {
            return "Qmusic";
        }
    },

    @XmlEnumValue("R538")
    R538(AUDIO, 1992) {
        @Override
        public String toString() {
            return "Radio 538";
        }
    },

    @XmlEnumValue("GOLD")
    GOLD(AUDIO, 1988) {
        @Override
        public String toString() {
            return "Radio 10 Gold";
        }
    },

    @XmlEnumValue("ARRO")
    ARRO(AUDIO, 1996) {
        @Override
        public String toString() {
            return "Arrow Classic Rock";
        }
    },

    @XmlEnumValue("FUNX")
    FUNX(AUDIO, 2002) {
        @Override
        public String toString() {
            return "FunX";
        }

        @Override
        public String pdId() {
            return "FUNX";
        }
    },

    @XmlEnumValue("FUNA")
    FUNA(AUDIO, null, null, true) {
        @Override
        public String toString() {
            return "FunX Amsterdam";
        }
    },


    @XmlEnumValue("FUNB")
    FUNB(AUDIO, null, null, true)  {
        @Override
        public String toString() {
            return "FunX Arab";
        }
    },


    @XmlEnumValue("FUND")
    FUND(AUDIO, null, null, true)  {
        @Override
        public String toString() {
            return "FunX Dance";
        }
    },

    @XmlEnumValue("FUNH")
    FUNH(AUDIO, null, null, true)  {
        @Override
        public String toString() {
            return "FunX HipHop";
        }
    },
     @XmlEnumValue("FUNL")
    FUNL(AUDIO, null, null, true)  {
        @Override
        public String toString() {
            return "FunX Latin";
        }
    },
    @XmlEnumValue("FUNJ")
    FUNJ(AUDIO, null, null, true)  {
        @Override
        public String toString() {
            return "FunX Raggae";
        }
    },
    @XmlEnumValue("FUNS")
    FUNS(AUDIO, null, null, true)  {
        @Override
        public String toString() {
            return "FunX SlowJamz";
        }
    },

    @XmlEnumValue("FUNR")
    FUNR(AUDIO, null, null, true)  {
        @Override
        public String toString() {
            return "FunX Rotterdam";
        }
    },

    @XmlEnumValue("FUNU")
    FUNU(AUDIO, null, null, true)  {
        @Override
        public String toString() {
            return "FunX Utrecht";
        }
    },

    @XmlEnumValue("FUNG")
    FUNG(AUDIO, null, "https://www.funx.nl/denhaag/online-radio-luisteren", true)  {
        @Override
        public String toString() {
            return "FunX Den Haag";
        }
    },


    /**
     * @since 5.27
     */
    @XmlEnumValue("FUNF")
    FUNF(AUDIO, null, "https://www.funx.nl/fissa/online-radio-luisteren", true)  {
        @Override
        public String toString() {
            return "FunX Fissa";
        }
    },

    @XmlEnumValue("CLAS")
    CLAS(AUDIO, 1994) {
        @Override
        public String toString() {
            return "Classic FM";
        }
    },

    @XmlEnumValue("BEL1")
    BEL1 {
        @Override
        public String toString() {
            return "VRT/Radio 1";
        }
    },

    @XmlEnumValue("BEL2")
    BEL2 {
        @Override
        public String toString() {
            return "VRT/Radio 2";
        }
    },

    @XmlEnumValue("KLAR")
    KLAR(AUDIO, 2000) {
        @Override
        public String toString() {
            return "Klara";
        }
    },

    @XmlEnumValue("BBR1")
    BBR1(AUDIO, 1967) {
        @Override
        public String toString() {
            return "BBC Radio 1";
        }
    },

    @XmlEnumValue("BBR2")
    BBR2(AUDIO, 1967) {
        @Override
        public String toString() {
            return "BBC Radio 2";
        }
    },

    @XmlEnumValue("BBR3")
    BBR3(AUDIO, "https://www.bbc.co.uk/sounds/play/live:bbc_radio_three", 1967) {
        @Override
        public String toString() {
            return "BBC Radio 3";
        }

    },

    @XmlEnumValue("BBR4")
    BBR4(AUDIO, 1967) {
        @Override
        public String toString() {
            return "BBC Radio 4";
        }
    },

    @XmlEnumValue("BBWS")
    BBWS(AUDIO, 1932) {
        @Override
        public String toString() {
            return "BBC Worldservice";
        }
    },

    @XmlEnumValue("BBCX")
    BBCX(AUDIO, "https://www.bbc.co.uk/sounds/play/live:bbc_1xtra", 2002) {
        @Override
        public String toString() {
            return "BBC 1Xtra";
        }
    },

    /**
     */
    @XmlEnumValue("NDR3")
    NDR3(VIDEO, 1956) {
        @Override
        public String toString() {
            return "NDR Fernsehen";
        }
    },

    @XmlEnumValue("WDR4")
    WDR4(AUDIO, 1984) {
        @Override
        public String toString() {
            return "WDR 4";
        }
    },

    @XmlEnumValue("WDR3")
    WDR3(AUDIO, 1964) {
        @Override
        public String toString() {
            return "WDR3";
        }
    },

    // TODO Check these codes
    @XmlEnumValue("D24K")
    D24K(VIDEO, 2011) {
        @Override
        public String toString() {
            return "24Kitchen";
        }

    },
    @XmlEnumValue("H1NL")
    H1NL {
        @Override
        public String toString() {
            return "HBO 1";
        }
    },
    @XmlEnumValue("SYFY")
    SYFY(VIDEO, 1992, 2016) { // syfy benelux?
        @Override
        public String toString () {
            return "Syfy";
        }
    },

    @XmlEnumValue("SBS9")
    SBS9(VIDEO, 2015) {
        @Override
        public String toString() {
            return "SBS 9";
        }
    },

    @XmlEnumValue("DIXD")
    DIXD {
        @Override
        public String toString() {
            return "Disney XD";
        }
    },
    @XmlEnumValue("BRNL")
    BRNL(VIDEO, "https://classica.stingray.com", 1995) {
        @Override
        public String toString() {
            return "Stringray Classica";
        }
    },
    @XmlEnumValue("BRHD")
    BRHD {
        @Override
        public String toString() {
            return "Brava HD";
        }
    },
    @XmlEnumValue("FOXL")
    FOXL{
        @Override
        public String toString() {
            return "Fox Live";
        }
    },
    @XmlEnumValue("TLC_")
    TLC_ {
        @Override
        public String toString() {
            return "TLC";
        }
    },

    @XmlEnumValue("VRTC")
    VRTC(VIDEO, 1997) {
        @Override
        public String toString() {
            return "VRT Canvas"; // Since 2012 zijn Ketnet en Canvas 2 kanalen.
        }
    },

    @XmlEnumValue("BCFS")
    BCFS {
        @Override
        public String toString() {
            return "BBC First";
        }
    },

    @XmlEnumValue("FXNL")
    FXNL {
        @Override
        public String toString () {
            return "Fox Nederland";
        }
    },
    @XmlEnumValue("AMC_")
    AMC_ {
        @Override
        public String toString() {
            return "AMC";
        }
    },
    @XmlEnumValue("FLM1")
    FLM1 {
        @Override
        public String toString() {
            return "Film 1";
        }
    },
    @XmlEnumValue("ZGS1")
    ZGS1 {
        @Override
        public String toString() {
            return "Ziggo Sport";
        }
    },
    @XmlEnumValue("BRTZ")
    BRTZ {
        @Override
        public String toString() {
            return "RTL Z";
        }
    },
    @XmlEnumValue("DVIC")
    DVIC {
        @Override
        public String toString() {
            return "Vice TV";
        }
    },

    /**
     * @since 5.11
     */
    @XmlEnumValue("DVB1")
    DVB1 {
        @Override
        public String toString() {
            return "PO DVB-H 1";
        }
    },

    /**
     * @since 5.11
     */
    @XmlEnumValue("DVB2")
    DVB2 {
        @Override
        public String toString() {
            return "PO DVB-H 2";
        }
    },

    /**
     * @since 5.11
     */
    @XmlEnumValue("DVB3")
    DVB3 {
        @Override
        public String toString() {
            return "PO DVB-H 3";
        }
    },

    /**
     * @since 5.11
     */
    @XmlEnumValue("ZIZO")
    ZIZO {
        @Override
        public String toString() {
            return "PO Zizone TV";
        }
    },

    /**
     * @since 5.11
     */
    @XmlEnumValue("FANT")
    FANT {
        @Override
        public String toString() {
            return "Fan TV";
        }
    },

    /**
     * @since 5.11
     */
    @XmlEnumValue("ONL1")
    ONL1(VIDEO) {
        @Override
        public String toString() {
            return "NPO Online 1";
        }
    },

    /**
     * @since 5.11
     */
    @XmlEnumValue("KPN1")
    KPN1(VIDEO) {
        @Override
        public String toString() {
            return "Eredivisie Live 1";
        }
    },
     /**
     * @since 5.11
     */
    @XmlEnumValue("KPN2")
    KPN2(VIDEO) {
        @Override
        public String toString() {
            return "Eredivisie Live 2";
        }
    },
     /**
     * @since 5.11
     */
    @XmlEnumValue("KPN3")
    KPN3(VIDEO) {
        @Override
        public String toString() {
            return "Eredivisie Live 3";
        }
    },
     /**
     * @since 5.11
     */
    @XmlEnumValue("KPN4")
    KPN4(VIDEO) {
        @Override
        public String toString() {
            return "Eredivisie Live 4";
        }
    },
    /**
     * @since 5.11
     */
    @XmlEnumValue("ZONH")
    ZONH(VIDEO) {
        @Override
        public String toString() {
            return "Zone Horror";
        }
    },
     /**
     * @since 5.11
     */
    @XmlEnumValue("XM24")
    XM24() {
        @Override
        public String toString() {
            return "X-MO DI";
        }
    },
     /**
     * @since 5.11
     */
    @XmlEnumValue("MNET")
    MNET() {
        @Override
        public String toString() {
            return "Misdaadnet";
        }
    },
     /**
     * @since 5.11
     */
    @XmlEnumValue("OU24")
    OU24() {
        @Override
        public String toString() {
            return "PO Out TV";
        }
    },
      /**
     * @since 5.11
     */
    @XmlEnumValue("POKE")
    POKE() {
        @Override
        public String toString() {
            return "Poker Channel";
        }
    },
      /**
     * @since 5.11
     */
    @XmlEnumValue("RACW")
    RACW() {
        @Override
        public String toString() {
            return "Raceworld TV";
        }
    },
      /**
     * @since 5.11
     */
    @XmlEnumValue("STTV")
    STTV() {
        @Override
        public String toString() {
            return "Star TV";
        }
    },
      /**
     * @since 5.11
     */
    @XmlEnumValue("TVPO")
    TVPO(VIDEO, 1992) {
        @Override
        public String toString() {
            return "TV Polonia";
        }
    },
    /**
     * @since 5.11
     */
    @XmlEnumValue("VOOM")
    VOOM() {
        @Override
        public String toString() {
            return "Voom HD";
        }
    },

    /**
     * @since 5.20
     */
    @XmlEnumValue("10TB")
    _10TB(VIDEO, 2006) {
        @Override
        public String toString() {
            return "NPO 1 extra";
        }
    },

    /**
     * @since 5.30
     */
    @XmlEnumValue("SPID")
    SPID(VIDEO, Range.closed(Year.of(2014), Year.of(2022)), "https://www.spiketv.nl/", null) {
        @Override
        public String toString() {
            return "Spike Nederland";
        }
    },

    /**
     * @since 7.3
     */
    @XmlEnumValue("PRMT")
    PRMT(VIDEO, "https://www.paramountnetwork.nl/", 2022) {
        @Override
        public String toString() {
            return "Paramount Network Nederland";
        }
    },

    /**
     * This is not a real channel. It can be used for testing or mocking purposes.
     *
     * @since 5.15
     */
    @XmlEnumValue("XXXX")
    XXXX() {
        @Override
        public String toString() {
            return "TEST CHANNEL";
        }
    },
    /**
     * This is not a real channel. It can be used for testing or mocking purposes.
     *
     * @since 7.10
     */
    @XmlEnumValue("XXXY")
    XXXY() {
        @Override
        public String toString() {
            return "TEST CHANNEL 2";
        }
    }
    ;
    private final AVType avType;

    @Getter
    private final Range<Year> range;

    @Getter
    private final String website;


    @Getter
    private final Boolean onlineOnly;

    Channel(AVType avType) {
        this.avType = avType;
        this.range = null;
        this.website = null;
        this.onlineOnly = null;
    }

    Channel(AVType avType, String website, int from) {
        this.avType = avType;
        this.range = Range.atLeast(Year.of(from));
        this.website = website;
        this.onlineOnly = null;
    }

    Channel(AVType avType, int from) {
        this(avType, null, from);
    }


    Channel(AVType avType, int from , int until) {
        this(avType, Range.closed(Year.of(from), Year.of(until)), null, null);
    }

    Channel(AVType avType, Range<Year> range, String website, Boolean onlineOnly) {
        this.avType = avType;
        this.range = range;
        this.website = website;
        this.onlineOnly = onlineOnly;
    }


    Channel() {
        this(null);
    }

    public String misId() {
        return getXmlValue();
    }

    public String pdId() {
        return getXmlValue();
    }

    public AVType getAVType() {
        return avType;
    }

    @Override
    public String getDisplayName() {
        return toString();
    }

    //@JsonValue (would fix ignored test case, but not backwards compatible)
    public final String getXmlEnumValue() {
        return getXmlValue();
    }

    public static Channel findByMisId(String misId) {
        for(Channel channel : values()) {
            if(channel.misId().equals(misId)) {
                return channel;
            }
        }
        return null;
    }


    public static Channel findByPDId(String epgId) {
        for(Channel channel : values()) {
            if(epgId.equals(channel.pdId())) {
                return channel;
            }
        }
        return null;
    }

    public static List<Channel> valuesOf(Collection<String> strings) {
        List<Channel> result = new ArrayList<>();
        for(String s : strings) {
            StringBuilder sBuilder = new StringBuilder(s);
            while(sBuilder.length() < 4) {
                sBuilder.append("_");
            }
            s = sBuilder.toString();
            try {
                result.add(valueOfXml(s));
            } catch (IllegalArgumentException iae) {
                result.add((valueOf(s)));
            }
        }
        return result;
    }

    public static Channel[] split(String channels) {
        final String[] values = channels.split("\\s*,\\s*");
        List<Channel> result = new ArrayList<>();
        for(String value : values) {
            if(Character.isDigit(value.charAt(0))) {
                value = "_" + value;
            }
            result.add(Channel.valueOf(value));
        }
        return result.toArray(new Channel[0]);
    }

    public static Channel valueOfIgnoreCase(String channel) {
        if (StringUtils.isBlank(channel)) {
            return null;
        }
        try {
            return valueOf(channel);
        } catch (IllegalArgumentException ignore) {
            return valueOf(channel.toUpperCase());
        }
    }

    public static Channel valueOfXml(String channel) {
        return XmlValued.valueOfXml(values(), channel);
    }

    public static class Deserializer extends BackwardsCompatibleJsonEnum.Deserializer<Channel> {
        public Deserializer() {
            super(Channel.class);
        }
    }

}
