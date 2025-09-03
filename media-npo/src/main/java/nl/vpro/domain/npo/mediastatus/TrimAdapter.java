package nl.vpro.domain.npo.mediastatus;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class TrimAdapter extends XmlAdapter<String, String> {
    @Override
    public String unmarshal(String v) {
        return v == null ? null : v.trim();
    }
    @Override
    public String marshal(String v) {
        return v;
    }
}
