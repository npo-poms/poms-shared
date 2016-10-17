package nl.vpro.media.odi.security;

import java.util.List;

public class OdiClient {

    private final String publicKey;

    private final List<String> origins;

    private final String secret;

    private final boolean allowXOrigin;

    public OdiClient(String publicKey, List<String> origins, String secret, boolean allowXOrigin) {
        this.publicKey = publicKey;
        this.origins = origins;
        this.secret = secret;
        this.allowXOrigin = allowXOrigin;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public List<String> getOrigins() {
        return origins;
    }

    public boolean matchesOrigin(String origin) {
        for (String originPattern : origins) {
            if (match(origin, originPattern)) {
                return true;
            }
        }
        return false;
    }

    private boolean match(String origin, String originPattern) {
        String[] sections = originPattern.split("\\*");
        String text = origin;
        for (String section : sections) {
            int index = text.indexOf(section);
            if (index == -1) {
                return false;
            }
            text = text.substring(index + section.length());
        }
        return true;
    }

    public String getSecret() {
        return secret;
    }

    public boolean isAllowXOrigin() {
        return allowXOrigin;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Client");
        sb.append("{publicKey='").append(publicKey).append('\'');
        sb.append(", origins='").append(origins).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OdiClient)) return false;

        OdiClient odiClient = (OdiClient) o;

        if (allowXOrigin != odiClient.allowXOrigin) return false;
        if (origins != null ? !origins.equals(odiClient.origins) : odiClient.origins != null) return false;
        if (publicKey != null ? !publicKey.equals(odiClient.publicKey) : odiClient.publicKey != null) return false;
        if (secret != null ? !secret.equals(odiClient.secret) : odiClient.secret != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = publicKey != null ? publicKey.hashCode() : 0;
        result = 31 * result + (origins != null ? origins.hashCode() : 0);
        return result;
    }


}