package nl.vpro.domain.api;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public class Examples {

    private Examples() {
    }

    public static final String IDLIST_JSON = "[\"AVRO_1656037\", \"NCRV_1413393\"]";
    public static final String IDLIST_XML = """
        <api:idList xmlns:api="urn:vpro:api:2013" >
            <api:id>AVRO_1656037</api:id>
            <api:id>NCRV_1413393</api:id>
        </api:idList>""";

    public static final String FORM_JSON = """
        {
            "searches" : {
                "text" : {
                        "value" : "Argos"
                }
            }
        }""";

    public static final String FORM_JSON_BROADCASTS = """
         {
             "searches" : {
                 "types" : "BROADCAST",
                 "avTypes": "VIDEO"
             }

        }
        """;

    public static final String FORM_XML = """
        <api:mediaForm xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
            <api:searches>
                <api:text fuzziness="AUTO" match="SHOULD">Argos</api:text>
            </api:searches>
        </api:mediaForm>""";

}
