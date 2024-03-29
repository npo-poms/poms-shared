//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2009.03.30 at 03:14:06 PM CEST
//


package nl.vpro.domain.media;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "locationTableType",
         propOrder = {"locations",
                      "scheduleEvents" })
public class LocationTable implements Serializable {

    @Serial
    private static final long serialVersionUID = 7215068410057727467L;

    @XmlElement(name = "location")
    protected List<@Valid Location> locations;
    @XmlElement(name = "scheduleEvent")
    protected List<@Valid ScheduleEvent> scheduleEvents;


    public List<Location> getLocations() {
        if(locations == null) {
            locations = new ArrayList<>();
        }
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<ScheduleEvent> getSchedule() {
        if(scheduleEvents == null) {
            scheduleEvents = new ArrayList<>();
        }
        return this.scheduleEvents;
    }

    public void setSchedule(List<ScheduleEvent> scheduleEvents) {
        this.scheduleEvents = scheduleEvents;
    }
}
