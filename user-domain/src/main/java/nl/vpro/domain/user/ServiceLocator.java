package nl.vpro.domain.user;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class ServiceLocator implements ApplicationContextAware {

    private static ApplicationContext context;

    private ServiceLocator() {
    }

    public static BroadcasterService getBroadcasterService() {
        return context.getBean(BroadcasterService.class);
    }

    public static PortalService getPortalService() {
        return context.getBean(PortalService.class);
    }

    public static ThirdPartyService getThirdPartyService() {
        return context.getBean(ThirdPartyService.class);
    }

    public static EditorService getEditorService() {
        return context.getBean(EditorService.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }


}
