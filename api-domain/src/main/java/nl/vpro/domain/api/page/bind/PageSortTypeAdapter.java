package nl.vpro.domain.api.page.bind;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import nl.vpro.domain.api.Order;
import nl.vpro.domain.api.page.PageSortField;
import nl.vpro.domain.api.page.PageSortType;
import nl.vpro.domain.api.page.PageSortTypeList;

/**
 * @TODO more or less copied from MediaSortTypeAdapter. It seems like rather a lot of code.
 * @author Michiel Meeuwissen
 * @since 2.3
 */
public class PageSortTypeAdapter extends XmlAdapter<PageSortTypeList, Map<PageSortField, Order>> {

    @Override
    public Map<PageSortField, Order> unmarshal(PageSortTypeList v) {
        LinkedHashMap<PageSortField, Order> answer = new LinkedHashMap<>(v.getSort().size());
        for (PageSortType sortType : v.getSort()) {
            answer.put(sortType.getField(), sortType.getOrder());
        }
        return answer;
    }

    @Override
    public PageSortTypeList marshal(Map<PageSortField, Order> v) {
        if (v == null) {
            return null;
        }

        List<PageSortType> answer = new ArrayList<>(v.size());
        for (Map.Entry<PageSortField, Order> entry : v.entrySet()) {
            answer.add(new PageSortType(entry.getKey(), entry.getValue()));
        }
        return new PageSortTypeList(answer);
    }

}

