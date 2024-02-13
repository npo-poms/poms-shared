package nl.vpro.domain.media.nebo.shared;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang3.StringUtils;

/**
 */
public class TimeAdapter extends XmlAdapter<String, Date> {

    protected SimpleDateFormat getDateFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UT"));
        return simpleDateFormat;
    }


    @Override
    public Date unmarshal(String v) throws Exception {
        if (StringUtils.isNotBlank(v)) {
            return getDateFormat().parse(v);
        } else {
            return null;
        }
    }

    @Override
    public String marshal(Date date) {
        if (date != null) {
            return getDateFormat().format(date);
        }
        return null;
    }
}
