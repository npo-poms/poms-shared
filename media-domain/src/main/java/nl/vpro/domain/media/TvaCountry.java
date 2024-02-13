package nl.vpro.domain.media;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.neovisionaries.i18n.CountryCode;

import nl.vpro.domain.media.bind.TvaCountryAdapter;

/**
 * See <a href="https://jira.vpro.nl/browse/MSE-2367">jira</a>, Appendix%203%20Country%20Codes%20v2.0.pdf
 * <p>
 * For now not used, we simply use the more complete {@link CountryCode}
 *
 * @author Michiel Meeuwissen
 * @since 3.0
 */
@XmlJavaTypeAdapter(value = TvaCountryAdapter.class)
public enum TvaCountry {
    AD("AD", "Andorrees", "Andorrese", "Andorrese"),
    AF("AF", "Afghaans", "Afghaanse", "Afghaanse"),
    AL("AL", "Albanees", "Albanese", "Albanese"),
    AN("AN", "Nederlands-Antilliaans", "Nederlands-Antilliaanse", "Nederlands-Antilliaanse"),
    AO("AO", "Angolees", "Angolese", "Angolese"),
    AR("AR", "Argentijns", "Argentijnse", "Argentijnse"),
    AT("AT", "Oostenrijks", "Oostenrijkse", "Oostenrijkse"),
    AU("AU", "Australisch", "Australische", "Australische"),
    BB("BB", "Barbadiaans", "Barbadiaanse", "Barbadiaanse"),
    BD("BD", "Bengalees", "Bengalese", "Bengalese"),
    BE("BE", "Belgisch", "Belgische", "Belgische"),
    BF("BF", "Burkinees", "Burkinese", "Burkinese"),
    BG("BG", "Bulgaars", "Bulgaarse", "Bulgaarse"),
    BH("BH", "Bahreins", "Bahreinse", "Bahreinse"),
    BI("BI", "Burundees", "Burundese", "Burundese"),
    BM("BM", "Bermudaans", "Bermudaanse", "Bermudaanse"),
    BN("BN", "Bruneis", "Bruneise", "Bruneise"),
    BO("BO", "Boliviaans", "Boliviaanse", "Boliviaanse"),
    BR("BR", "Braziliaans", "Braziliaanse", "Braziliaanse"),
    BS("BS", "Bahamaans", "Bahamaanse", "Bahamaanse"),
    BW("BW", "Botswaans", "Botswaanse", "Botswaanse"),
    CA("CA", "Canadees", "Canadese", "Canadese"),
    CF("CF", "Centraal-Afrikaans", "Centraal-Afrikaanse", "Centraal-Afrikaanse"),
    CG("CG", "Congolees", "Congolese", "Congolese"),
    CH("CH", "Zwitsers", "Zwitserse", "Zwitserse"),
    CI("CI", "Ivoriaans", "Ivoriaanse", "Ivoriaanse"),
    CL("CL", "Chileens", "Chileense", "Chileense"),
    CM("CM", "Kameroens", "Kameroense", "Kameroense"),
    CN("CN", "Chinees", "Chinese", "Chinese"),
    CO("CO", "Colombiaans", "Colombiaanse", "Colombiaanse"),
    CR("CR", "Costaricaans", "Costaricaanse", "Costaricaanse"),
    CS(null, "Tjecho-Slowaaks", "Tjecho-Slowaakse", "Tjecho-Slowaakse"),
    CU("CU", "Cubaans", "Cubaanse", "Cubaanse"),
    CY("CY", "Cypriotisch", "Cypriotische", "Cypriotische"),
    CZ("CZ", "Tsjechisch", "Tsjechische", "Tsjechische"),
    DE("DE", "Duitsland", "Duits", "Duitse"),
    DK("DK", "Denemarken", "Deens", "Deense"),
    DM("DM", "Dominica", "Dominicaans", "Dominicaanse"),
    DO("DO", "Dominicaanse Republiek", "Dominicaans", "Dominicaanse"),
    DZ("DZ", "Algerije", "Algerijns", "Algerijnse"),
    EA("EA", "Oost-Duitsland", "Oost-Duits", "Oost-Duitse"),
    EC("EC", "Ecuador", "Ecuadoraans", "Ecuadoraanse"),
    EE("EE", "Estland", "Estlands", "Estlandse"),
    EG("EG", "Egypte", "Egyptisch", "Egyptische"),
    ES("ES", "Spanje", "Spaans", "Spaanse"),
    ET("ET", "Ethiopië", "Ethiopisch", "Ethiopische"),
    FI("FI", "Finland", "Fins", "Finse"),
    FJ("FJ", "Fiji", "Fijisch", "Fijische"),
    FO("FO", "Faeröer", "Faeröers", "Faeröerse"),
    FR("FR", "Frankrijk", "Frans", "Franse"),
    GB("GB", "Groot-Brittannië", "Brits", "Britse"),
    GD("GD", "Grenada", "Grenadiaans", "Grenadiaanse"),
    GF("GF", "Frans-Guyana", "Frans-Guyaans", "Frans-Guyaanse"),
    GH("GH", "Ghana", "Ghanees", "Ghanese"),
    GM("GM", "Gambia", "Gambiaans", "Gambiaanse"),
    GR("GR", "Griekenland", "Grieks", "Griekse"),
    GT("GT", "Guatemala", "Guatemalteeks", "Guatemalteekse"),
    GY("GY", "Guyana", "Guyaans", "Guyaanse"),
    HK("HK", "Hongkong", "Hongkongs", "Hongkongse"),
    HN("HN", "Honduras", "Hondurees", "Hondurese"),
    HR("HR", "Kroatië", "Kroatisch", "Kroatische"),
    HT("HT", "Haïti", "Haïtiaans", "Haïtiaanse"),
    HU("HU", "Hongarije", "Hongaars", "Hongaarse"),
    ID("ID", "Indonesië", "Indonesisch", "Indonesische"),
    IE("IE", "Ierland", "Iers", "Ierse"),
    IL("IL", "Israël", "Israëlisch", "Israëlische"),
    IN("IN", "India", "Indiaas", "Indiaase"),
    IQ("IQ", "Irak", "Iraaks", "Iraakse"),
    IR("IR", "Iran", "Iraans", "Iraanse"),
    IS("IS", "IJsland", "IJslands", "IJslandse"),
    IT("IT", "Italië", "Italiaans", "Italiaanse"),
    JM("JM", "Jamaica", "Jamaicaans", "Jamaicaanse"),
    JO("JO", "Jordanië", "Jordaans", "Jordaanse"),
    JP("JP", "Japan", "Japans", "Japanse"),
    KE("KE", "Kenia", "Keniaans", "Keniaanse"),
    KH("KH", "Cambodja", "Cambodjaans", "Cambodjaanse"),
    KP("KP", "Noord-Korea", "Noord-Koreaans", "Noord-Koreaanse"),
    KR("KR", "Zuid-Korea", "Zuid-Koreaans", "Zuid-Koreaanse"),
    KW("KW", "Koeweit", "Koeweits", "Koeweitse"),
    LA("LA", "Laos", "Laotiaans", "Laotiaanse"),
    LB("LB", "Libanon", "Libanees", "Libanese"),
    LI("LI", "Liechtenstein", "Liechtensteins", "Liechtensteinse"),
    LK("LK", "Sri Lanka", "Srilankaans", "Srilankaanse"),
    LR("LR", "Liberia", "Liberiaans", "Liberiaanse"),
    LS("LS", "Lesotho", "Lesothaans", "Lesothaanse"),
    LU("LU", "Luxemburg", "Luxemburgs", "Luxemburgse"),
    LY("LY", "Libië", "Libisch", "Libische"),
    MA("MA", "Marokko", "Marokkaans", "Marokkaanse"),
    MC("MC", "Monaco", "Monegaskisch", "Monegaskische"),
    MG("MG", "Madagaskar", "Malagassisch", "Malagassische"),
    MK("MK", "Macedonië", "Macedonisch", "Macedonische"),
    ML("ML", "Mali", "Malinees", "Malinese"),
    MM("MM", "Birma", "Birmees", "Birmese"),
    MR("MR", "Mauritanië", "Mauritaans", "Mauritaanse"),
    MT("MT", "Malta", "Maltees", "Maltese"),
    MU("MU", "Mauritius", "Mauritiaans", "Mauritiaanse"),
    MW("MW", "Malawi", "Malawisch", "Malawische"),
    MY("MY", "Maleisië", "Maleisisch", "Maleisische"),
    NA("NA", "Namibië", "Namibisch", "Namibische"),
    NE("NE", "Niger", "Nigerees", "Nigerese"),
    NG("NG", "Nigeria", "Nigeriaans", "Nigeriaanse"),
    NI("NI", "Nicaragua", "Nicaraguaans", "Nicaraguaanse"),
    NL("NL", "Nederland", "Nederlands", "Nederlandse"),
    NO("NO", "Noorwegen", "Noors", "Noorse"),
    NZ("NZ", "Nieuw-Zeeland", "Nieuw-Zeelands", "Nieuw-Zeelandse"),
    PA("PA", "Panama", "Panamees", "Panamese"),
    PE("PE", "Peru", "Peruaans", "Peruaanse"),
    PG("PG", "Papoea-Nieuw-Guinea", "Papoea-Nieuw-Guinees", "Papoea-Nieuw-Guinese"),
    PH("PH", "Filipijnen", "Filipijns", "Filipijnse"),
    PK("PK", "Pakistan", "Pakistaans", "Pakistaanse"),
    PL("PL", "Polen", "Pools", "Poolse"),
    PR("PR", "Puerto Rico", "Puerto Ricaans", "Puerto Ricaanse"),
    PT("PT", "Portugal", "Portugees", "Portugese"),
    PY("PY", "Paraguay", "Paraguayaans", "Paraguayaanse"),
    RO("RO", "Roemenië", "Roemeens", "Roemeense"),
    RU("RU", "Rusland", "Russisch", "Russische"),
    RW("RW", "Rwanda", "Rwandees", "Rwandese"),
    SA("SA", "Saudi-Arabië", "Saudisch", "Saudische"),
    SD("SD", "Soedan", "Sudanees", "Sudanese"),
    SE("SE", "Zweden", "Zweeds", "Zweedse"),
    SG("SG", "Singapore", "Singaporees", "Singaporese"),
    SK("SK", "Slowakije", "Slowaaks", "Slowaakse"),
    SL("SL", "Sierra Leone", "Sierraleoons", "Sierraleoonse"),
    SM("SM", "San Marino", "Sanmarinees", "Sanmarinese"),
    SN("SN", "Senegal", "Senegalees", "Senegalese"),
    SO("SO", "Somalië", "Somalisch", "Somalische"),
    SR("SR", "Suriname", "Surinaams", "Surinaamse"),
    SU(null, "Sovjetunie", "Russisch", "Russische"),
    SV("SV", "El Salvador", "Salvadoraans", "Salvadoraanse"),
    SY("SY", "Syrië", "Syrisch", "Syrische"),
    SZ("SZ", "Swaziland", "Swazisch", "Swazische"),
    TG("TG", "Togo", "Togolees", "Togolese"),
    TH("TH", "Thailand", "Thais", "Thaise"),
    TN("TN", "Tunesië", "Tunesisch", "Tunesische"),
    TR("TR", "Turkije", "Turks", "Turkse"),
    TW("TW", "Taiwan", "Taiwanees", "Taiwanese"),
    TZ("TZ", "Tanzania", "Tanzaniaans", "Tanzaniaanse"),
    UA("UA", "Oekraïne", "Oekraïens", "Oekraïense"),
    UG("UG", "Uganda", "Ugandees", "Ugandese"),
    US("US", "Verenigde Staten", "Amerikaans", "Amerikaanse"),
    UY("UY", "Uruguay", "Urugayaans", "Urugayaanse"),
    VE("VE", "Venezuela", "Venezolaans", "Venezolaanse"),
    VN("VN", "Vietnam", "Vietnamees", "Vietnamese"),
    WS("WS", "West-Samoa", "Samoaans", "Samoaanse"),
    YE("YE", "Zuid-Jemen", "Zuid-Jemenitisch", "Zuid-Jemenitische"),
    YU(null, "Joegoslavië", "Joegoslavisch", "Joegoslavische"),
    ZA("ZA", "Zuid-Afrika", "Zuid-Afrikaans", "Zuid-Afrikaanse"),
    ZR(null, "Zaire", "Zairees", "Zairese"),
    ZW("ZW", "Zimbabwe", "Zimbabwaans", "Zimbabwaanse");

    private final String isoCode;
    private final String dutchCountryName;
    private final String dutchNeuterAdjective;
    private final String dutchCommonGenderAdjective;
    TvaCountry(String isoCode, String dutchCountryName, String dutchNeuterAdjective, String dutchCommonGenderAdjective) {
        this.isoCode = isoCode;
        this.dutchCountryName = dutchCountryName;
        this.dutchNeuterAdjective = dutchNeuterAdjective;
        this.dutchCommonGenderAdjective = dutchCommonGenderAdjective;
    }

    public String getIsoCode() {
        return isoCode;
    }


    public String getIso3Code() {
        try {
            return isoCode == null ? null : new Locale("en", isoCode).getISO3Country();
        } catch (MissingResourceException mrs) {
            return isoCode;
        }
    }

    public static TvaCountry valueOf(CountryCode code) {
        return valueOf(code.getAlpha2());
    }

    public static TvaCountry find(String v) {
        try {
            return TvaCountry.valueOf(v);
        } catch (IllegalArgumentException iae) {
            for (TvaCountry c : TvaCountry.values()) {
                if (v.equals(c.getIso3Code())) {
                    return c;
                }
            }
            throw iae;
        }
    }

    public String getDutchCountryName() {
        return dutchCountryName;
    }

    public String getDutchNeuterAdjective() {
        return dutchNeuterAdjective;
    }

    public String getDutchCommonGenderAdjective() {
        return dutchCommonGenderAdjective;
    }

    public static List<TvaCountry> valueOf(List<String> values) {
        List<TvaCountry> result = new ArrayList<>(values.size());
        for (String v : values) {
            result.add(valueOf(v));
        }
        return result;
    }
}
