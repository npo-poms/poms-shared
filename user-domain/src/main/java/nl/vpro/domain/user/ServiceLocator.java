package nl.vpro.domain.user;

import java.util.*;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Static access to some services, for places where injection is not possible (e.g. in validators and in domain objects).
 * <p>
 * This is the one place where the {@link BroadcasterService} is registered. ({@code nl.vpro.media.broadcaster.BroadcasterServiceLocator} delegates to this)
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class ServiceLocator  {

    /**
     * Used if no broadcaster service is configured: a service without any broadcasters.
     */
    private static final Provider<BroadcasterService> DEFAULT_BROADCASTER_SERVICE = () -> new BroadcasterServiceImpl(new Broadcaster[0]);

    @Inject
    Provider<BroadcasterService> broadcasterService = DEFAULT_BROADCASTER_SERVICE;

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

    /**
     * @return The configured broadcaster service, or a service without any broadcasters if none is configured.
     * @see #findBroadcasterService()
     */
    @NonNull
    public static BroadcasterService getBroadcasterService() {
        return findBroadcasterService().orElseGet(DEFAULT_BROADCASTER_SERVICE::get);
    }

    /**
     * @return The broadcaster service, if one is configured (by injection, or via {@link #setBroadcasterService(BroadcasterService)})
     * @since 8.18
     */
    public static Optional<BroadcasterService> findBroadcasterService() {
        final ServiceLocator locator = serviceLocator;
        if (locator == null || locator.broadcasterService == DEFAULT_BROADCASTER_SERVICE) {
            return Optional.empty();
        }
        return Optional.ofNullable(locator.broadcasterService.get());
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


    /**
     * @param broadcasterService The broadcaster service to use. {@code null} resets to the default (a service without any broadcasters).
     */
    public static void setBroadcasterService(final BroadcasterService broadcasterService) {
        if (serviceLocator == null) {
            serviceLocator = new ServiceLocator();
        }
        serviceLocator.broadcasterService = broadcasterService == null ? DEFAULT_BROADCASTER_SERVICE : () -> broadcasterService;
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
