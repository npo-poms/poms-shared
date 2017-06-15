package nl.vpro.filter;

import java.io.IOException;

import javax.servlet.*;

import nl.vpro.esper.event.MediaEvent;

/**
 * @author Michiel Meeuwissen
 * @since 3.7
 */
public class WithEventSourceFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try (MediaEvent.Reset r = MediaEvent.withEventSource(MediaEvent.MediaEventSource.WEB)) {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
