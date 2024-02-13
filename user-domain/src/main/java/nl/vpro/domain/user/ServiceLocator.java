package nl.vpro.domain.user;

import java.util.*;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class ServiceLocator  {

    @Inject
    Provider<BroadcasterService> broadcasterService = () -> new BroadcasterServiceImpl(new Broadcaster[0]);

    @Inject
    Provider<PortalService> portalService = PortalServiceImpl::new;

    @Inject
    Provider<ThirdPartyService> thirdPartyService = ThirdPartyServiceImpl::new;

    @Inject
    Provider<EditorService> editorService = () -> null;

    private static ServiceLocator serviceLocator;

    private ServiceLocator() {
        serviceLocator = this;
    }

    @NonNull
    public static BroadcasterService getBroadcasterService() {
        return serviceLocator == null ? new BroadcasterServiceImpl(new Broadcaster[0]) : serviceLocator.broadcasterService.get();
    }

    @NonNull
    public static PortalService getPortalService() {
        return serviceLocator == null ? new PortalServiceImpl() : serviceLocator.portalService.get();
    }

    @NonNull
    public static ThirdPartyService getThirdPartyService() {
        return serviceLocator == null ? new ThirdPartyServiceImpl() : serviceLocator.thirdPartyService.get();
    }

    @NonNull
    public static EditorService getEditorService() {
        if (serviceLocator == null) {
            throw new IllegalStateException();
        }
        return serviceLocator.editorService.get();
    }

    public static void  setEditorService(final EditorService editorService) {
         if (serviceLocator == null) {
            serviceLocator = new ServiceLocator();
        }
        serviceLocator.editorService = () -> editorService;
    }


    public static void setBroadcasterService(final BroadcasterService broadcasterService) {
        if (serviceLocator == null) {
            serviceLocator = new ServiceLocator();
        }
        serviceLocator.broadcasterService = () -> broadcasterService;
    }

    /**
     * Just sets up a broadcasters service that recognizes the given broadcasters. Mainly useful in test cases.
     */
    public static void setBroadcasterService(String... broadcasters) {
        setBroadcasterService(new BroadcasterServiceImpl(broadcasters));
    }

    /**
     * Just sets up a broadcasters service that recognizes the given broadcasters. Mainly useful in test cases.
     */
    public static void setBroadcasterService(Broadcaster... broadcasters) {
        setBroadcasterService(new BroadcasterServiceImpl(broadcasters));
    }

    public static void setPortalService(final PortalService portalService) {
        if (serviceLocator == null) {
            serviceLocator = new ServiceLocator();
        }
        serviceLocator.portalService = () -> portalService;
    }

    public static void setThirdPartyService(final ThirdPartyService thirdPartyService) {
        if (serviceLocator == null) {
            serviceLocator = new ServiceLocator();
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

        @SafeVarargs
        protected AbstractOrganizationService(T... organizations) {
            repository.addAll(Arrays.asList(organizations));
        }

        @Override
        public T find(@NonNull String id) {
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


        public BroadcasterServiceImpl(Broadcaster... organizations) {
            super(organizations);
        }

        public BroadcasterServiceImpl(String... organizations) {
            super(Arrays.stream(organizations).map(Broadcaster::new).toArray(Broadcaster[]::new));
        }

    }
    public static class PortalServiceImpl extends AbstractOrganizationService<Portal> implements PortalService  {

        public PortalServiceImpl(Portal... organizations) {
            super(organizations);
        }

    }
     public static class ThirdPartyServiceImpl extends AbstractOrganizationService<ThirdParty> implements ThirdPartyService  {
         public ThirdPartyServiceImpl(ThirdParty... organizations) {
            super(organizations);
        }
    }

}
