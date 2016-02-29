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
        return serviceLocator.broadcasterService.get();
    }

    public static PortalService getPortalService() {
        return serviceLocator.portalService.get();
    }

    public static ThirdPartyService getThirdPartyService() {
        return serviceLocator.thirdPartyService.get();
    }

    public static EditorService getEditorService() {
        return serviceLocator.editorService.get();
    }


}
