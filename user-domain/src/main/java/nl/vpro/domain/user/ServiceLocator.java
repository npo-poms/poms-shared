package nl.vpro.domain.user;

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

    public static BroadcasterService getBroadcasterService() {
        return serviceLocator == null ? null : serviceLocator.broadcasterService.get();
    }

    public static PortalService getPortalService() {
        return serviceLocator == null ? null : serviceLocator.portalService.get();
    }

    public static ThirdPartyService getThirdPartyService() {
        return serviceLocator == null ? null : serviceLocator.thirdPartyService.get();
    }

    public static EditorService getEditorService() {
        return serviceLocator == null ? null : serviceLocator.editorService.get();
    }


}
