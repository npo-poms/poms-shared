package nl.vpro.api;

import javax.inject.Singleton;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import nl.vpro.domain.api.RepositoryType;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
@Component
@ManagedResource(objectName = "nl.vpro.api:name=Settings")
@Singleton
public class Settings {


    @Value("${mediaService.loadRepository}")
    public RepositoryType loadRepository = RepositoryType.COUCHDB;

    @Value("${mediaService.listRepository}")
    public  RepositoryType listRepository = RepositoryType.COUCHDB;

    @Value("${mediaService.changesRepository}")
    public RepositoryType changesRepository = RepositoryType.COUCHDB;


    @Value("${mediaService.redirectsRepository}")
    public RepositoryType redirectsRepository = RepositoryType.COUCHDB;


    @Value("${mediaService.membersRepository}")
    public RepositoryType membersRepository = RepositoryType.COUCHDB;


    @Value("${scheduleService.repository}")
    public RepositoryType scheduleRepository = RepositoryType.COUCHDB;



    // JMX sucks a bit, no support for fields or enums.
    @ManagedAttribute
    public String getLoadRepository() {
        return loadRepository.name();
    }

    @ManagedAttribute
    public void setLoadRepository(String loadRepository) {
        this.loadRepository = RepositoryType.valueOf(loadRepository);
    }

    @ManagedAttribute
    public String getListRepository() {
        return listRepository.name();
    }

    @ManagedAttribute
    public void setListRepository(String listRepository) {
        this.listRepository = RepositoryType.valueOf(listRepository);
    }

    @ManagedAttribute
    public String getChangesRepository() {
        return changesRepository.name();
    }

    @ManagedAttribute
    public void setChangesRepository(String changesRepository) {
        this.changesRepository = RepositoryType.valueOf(changesRepository);
    }

    @ManagedAttribute
    public String getRedirectsRepository() {
        return redirectsRepository.name();
    }

    @ManagedAttribute
    public void setRedirectsRepository(String redirectsRepository) {
        this.redirectsRepository = RepositoryType.valueOf(redirectsRepository);
    }

    @ManagedAttribute
    public String getMembersRepository() {
        return membersRepository.name();
    }

    @ManagedAttribute
    public void setMembersRepository(String  membersRepository) {
        this.membersRepository = RepositoryType.valueOf(membersRepository);
    }


    @ManagedAttribute
    public String getScheduleRepository() {
        return scheduleRepository.name();
    }

    @ManagedAttribute
    public void setScheduleRepository(String scheduleRepository) {
        this.scheduleRepository = RepositoryType.valueOf(scheduleRepository);
    }
}
