package nl.vpro.domain.api.page;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.api.*;
import nl.vpro.domain.api.media.MediaForm;
import nl.vpro.domain.api.page.bind.PageSortTypeAdapter;
import nl.vpro.domain.page.Page;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlRootElement(name = "pagesForm")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pagesFormType",
    propOrder = {
        "searches", "sortFields", "facets", "mediaForm"
    })
public class PageForm implements SortableForm, Predicate<Page> {


    public static PageFormBuilder builder() {
        return PageFormBuilder.form();
    }


    @JsonProperty("highlight")
    @XmlAttribute(name = "highlight")
    private Boolean highlighted = null;

    @XmlElement
    @Valid
    private PageSearch searches;

    @XmlElement
    @XmlJavaTypeAdapter(PageSortTypeAdapter.class)
    @JsonIgnore
    private LinkedHashMap<PageSortField, Order> sortFields;

    @XmlElement
    @Valid
    private PageFacets facets;

    @XmlElement
    @Valid
    private MediaForm mediaForm;

    @Override
    public boolean isFaceted() {
        return facets != null && facets.isFaceted();
    }

    /**
     * Returns the text search in {@link #getSearches()}
     */
    @Override
    public String getText() {
        return FormUtils.getText(searches);

    }

    public PageFacets getFacets() {
        return facets;
    }

    public void setFacets(PageFacets facets) {
        this.facets = facets;
    }

    public MediaForm getMediaForm() {
        return mediaForm;
    }

    public void setMediaForm(MediaForm mediaForm) {
        this.mediaForm = mediaForm;
    }

    @Override
    public boolean isSorted() {
        return sortFields != null && !sortFields.isEmpty();
    }

    @JsonProperty("sort")
    public Map<PageSortField, Order> getSortFields() {
        return sortFields;
    }

    public void setSortFields(LinkedHashMap<PageSortField, Order> sortFields) {
        this.sortFields = sortFields;
    }

    public void addSortField(PageSortField field) {
        addSortField(field, null);
    }

    public void addSortField(PageSortField field, Order order) {
        if (sortFields == null) {
            sortFields = new LinkedHashMap<>(3);
        }

        sortFields.put(field, order);
    }


    @Override
    public boolean isHighlight() {
        return highlighted != null ? highlighted : false;
    }

    public void setHighlight(boolean highlight) {
        this.highlighted = highlight ? true : null;
    }

    public PageSearch getSearches() {
        return searches;
    }

    public void setSearches(PageSearch searches) {
        this.searches = searches;
    }

    @Override
    public boolean test(@Nullable Page input) {
        return searches == null || searches.test(input);
    }

    @Override
    public String toString() {
        return "PageForm{" +
            "highlighted=" + highlighted +
            ", searches=" + searches +
            ", sortFields=" + sortFields +
            ", facets=" + facets +
            ", mediaForm=" + mediaForm +
            '}';
    }
}
