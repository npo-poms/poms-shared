package nl.vpro.domain.api;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public class Examples {

    private Examples() {

    }

    public static final String IDLIST_JSON = "[\"AVRO_1656037\", \"NCRV_1413393\"]";
    public static final String IDLIST_XML = "<api:idList xmlns:api=\"urn:vpro:api:2013\" >\n" +
        "            \"    <api:id>AVRO_1656037</api:id>\\n\" +\n" +
        "            \"    <api:id>NCRV_1413393</api:id>\\n\" +\n" +
        "            \"</api:idList>";

  /*  public static final Example IDLIST = {
    @ExampleProperty(mediaType = "application/json", value = Examples.IDLIST_JSON),
    @ExampleProperty(mediaType = "application/xml", value = Examples.IDLIST_XML)
};*/

    public static final String FORM_JSON = "{\n" +
        "    \"searches\" : {\n" +
        "        \"text\" : {\n" +
        "                \"value\" : \"Argos\"\n" +
        "        }\n" +
        "    }\n" +
        "}";
    public static final String FORM_XML = "{\n" +
        "    \"searches\" : {\n" +
        "        \"text\" : {\n" +
        "                \"value\" : \"Argos\"\n" +
        "        }\n" +
        "    }\n" +
        "}";

}
