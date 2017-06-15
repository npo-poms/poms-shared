package nl.vpro.web.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * @author Michiel Meeuwissen
 * @since 3.5
 */
@ManagedResource(
    description = "Service for ext interface",
    objectName = "nl.vpro.media:name=extMediaService"
)
public class MBean {

    @Autowired
    ExtMediaService extMediaService;

    @ManagedAttribute
    public boolean getUseIndexForSearch() {
        return extMediaService.getUseIndexForSearch();
    }

    @ManagedOperation
    public void setUseIndexForSearch(boolean useIndexForSearch) {
        extMediaService.setUseIndexForSearch(useIndexForSearch);
    }

    @ManagedAttribute
    public boolean getUseIndexForMembers() {
        return extMediaService.getUseIndexForMembers();
    }

    @ManagedOperation
    public void setUseIndexForMembers(boolean useIndexForMembers) {
        extMediaService.setUseIndexForMembers(useIndexForMembers);
    }
}
