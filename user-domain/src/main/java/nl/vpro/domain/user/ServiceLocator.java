package nl.vpro.domain.user;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class ServiceLocator  {


    @Inject
    Provider<BroadcasterService> broadcasterService = () -> null;

    @Inject
    Provider<PortalService> portalService = () -> null;

    @Inject
    Provider<ThirdPartyService> thirdPartyService = () -> null;

    @Inject
    Provider<EditorService> editorService = () -> null;

    private static ServiceLocator serviceLocator;

    private ServiceLocator() {
        serviceLocator = this;
    }

    @Nonnull
    public static BroadcasterService getBroadcasterService() {
        return serviceLocator == null ? new BroadcasterServiceImpl() : serviceLocator.broadcasterService.get();
    }

    @Nonnull
    public static PortalService getPortalService() {
        return serviceLocator == null ? new PortalServiceImpl() : serviceLocator.portalService.get();
    }

    @Nonnull
    public static ThirdPartyService getThirdPartyService() {
        return serviceLocator == null ? new ThirdPartyServiceImpl() : serviceLocator.thirdPartyService.get();
    }

    public static EditorService getEditorService() {
        return serviceLocator == null ? null : serviceLocator.editorService.get();
    }


    public static void setBroadcasterService(final BroadcasterService broadcasterService) {
        if (serviceLocator == null) {
            new ServiceLocator();
        }
        serviceLocator.broadcasterService = () -> broadcasterService;
    }

    public static void setPortalService(final PortalService portalService) {
        if (serviceLocator == null) {
            new ServiceLocator();
        }
        serviceLocator.portalService = () -> portalService;
    }

    public static void setThirdPartyService(final ThirdPartyService thirdPartyService) {
        if (serviceLocator == null) {
            new ServiceLocator();
        }
        serviceLocator.thirdPartyService = () -> thirdPartyService;
    }

    public static void getEditorService(EditorService editorService) {
        if (serviceLocator == null) {
            new ServiceLocator();
        }
        serviceLocator.editorService = () -> editorService;
    }


    protected static abstract  class AbstractOrganizationService<T extends Organization> implements OrganizationService<T> {

        private final List<T> repository = new ArrayList<>();

        @Override
        public T find(@Nonnull String id) {
            return repository.stream().filter(o -> o.getId().equals(id)).findFirst().orElse(null);

        }

        @Override
        public List<T> findAll() {
            return repository;

        }

        @Override
        public T update(T organization) {
            repository.remove(find(organization.getId()));
            repository.add(organization);
            return organization;

        }

        @Override
        public void delete(T organization) {
            repository.remove(find(organization.getId()));
        }
    }
    public static class BroadcasterServiceImpl extends AbstractOrganizationService<Broadcaster> implements BroadcasterService  {

    }
    public static class PortalServiceImpl extends AbstractOrganizationService<Portal> implements PortalService  {

    }
     public static class ThirdPartyServiceImpl extends AbstractOrganizationService<ThirdParty> implements ThirdPartyService  {

    }

}
