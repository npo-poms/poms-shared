/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.SortedSet;

import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.search.LocationSearchResult;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "locations")
public class LocationList extends TransferList<LocationView> {

    public LocationList() {
    }

    public LocationList(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static LocationList create(LocationSearchResult searchResult) {
        LocationList simpleList = new LocationList();
        simpleList.success = true;

        for(Location location : searchResult.getResult()) {
            simpleList.add(LocationView.create(location));
        }

        simpleList.results = searchResult.getCount();
        return simpleList;
    }

    public static LocationList create(MediaObject media) {
        SortedSet<Location> fullList = media.getLocations();

        LocationList simpleList = new LocationList();
        simpleList.success = true;

        if(fullList == null) {
            return simpleList;
        }
        
        for(Location location : fullList) {
            simpleList.add(LocationView.create(location));
        }

        return simpleList;
    }
}
