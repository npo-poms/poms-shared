package nl.vpro.domain;

public interface PersonInterface {

    String getGivenName();

    String getFamilyName();

    String getGtaaUri();

    /**
     * @since 5.12
     */
    default String getName() {
        String giveName = getGivenName();
        String familyName = getFamilyName();
        return stringValue(giveName, familyName);

    }

    /**
     * @since 5.11
     */
    static String stringValue(String givenName, String familyName) {
        if (familyName == null && givenName == null) {
            return null;
        }
        return (familyName == null ? "" : familyName) + (givenName == null ? "":  ", " + givenName);
    }

    /**
     * Returns an array with {'familyName', 'givenName'}
     * @since 5.12
     */
    static String[] parseName(String name) {
        if (name != null) {
            String[] split = name.split("\\s*,\\s*", 2);
            if (split.length == 1) {
                return new String[] {name, null};
            } else {
                return new String[] {split[0], split[1]};
            }
        } else {
             return new String[] {null, null};
         }
    }

}
