/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media.bind;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import nl.vpro.domain.api.Order;
import nl.vpro.domain.api.media.MediaSortField;
import nl.vpro.domain.api.media.MediaSortType;
import nl.vpro.domain.api.media.MediaSortTypeList;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class MediaSortTypeAdapter extends XmlAdapter<MediaSortTypeList, LinkedHashMap<MediaSortField, Order>> {

    @Override
    public LinkedHashMap<MediaSortField, Order> unmarshal(MediaSortTypeList v) throws Exception {
        LinkedHashMap<MediaSortField, Order> answer = new LinkedHashMap<>(v.getSort().size());
        for(MediaSortType sortType : v.getSort()) {
            answer.put(sortType.getField(), sortType.getOrder());
        }
        return answer;
    }

    @Override
    public MediaSortTypeList marshal(LinkedHashMap<MediaSortField, Order> v) throws Exception {
        if(v == null) {
            return null;
        }

        List<MediaSortType> answer = new ArrayList<>(v.size());
        for(Map.Entry<MediaSortField, Order> entry : v.entrySet()) {
            answer.add(new MediaSortType(entry.getKey(), entry.getValue()));
        }
        return new MediaSortTypeList(answer);
    }

}
