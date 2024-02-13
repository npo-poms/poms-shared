/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.security;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.util.ThreadPools;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
@Aspect
@Slf4j
public class OdiAuthentication {
    public static final String X_NPO_DATE = "x-npo-date";

    public static final String X_NPO_MID = "x-npo-mid";

    public static final String X_NPO_URL = "x-npo-url";

    public static final String X_ORIGIN = "x-origin";

    public static final String AUTHORIZATION = "authorization";

    //per line format "apiKey:privateKey:allowXOrigin:origins , separated with * support"
    private static final Pattern CONFIG_PATTERN = Pattern.compile("^(\\w+):([^:]+):(false|true):(.+)$");

    private static final Pattern HEADER_PATTERN = Pattern.compile("\\s*NPO\\s+(\\w+):(.+)\\s*$");

    private static final String CONFIG_FILE = "odi.clients";

    private String configFolder;

    private long expiresInMinutes = 10;

    private  Set<OdiClient> clients = null;

    private final ExecutorService executorService =
        Executors.newSingleThreadExecutor(ThreadPools.createThreadFactory("OdiConfigMonitor", true, Thread.NORM_PRIORITY));

    private boolean running = true;

    @PostConstruct
    public void init() {
        loadAuthorizedClients();
        appendConfigWatcher();
    }
    @PreDestroy
    public void shutdown() {
        running = false;
        executorService.shutdownNow();
    }

    @Before("target(nl.vpro.media.odi.OdiService) && execution(* *(..)) && args(media, request, ..)")
    public void handleMedia(MediaObject media, HttpServletRequest request) {
        request = patchIE89(request);

        hasRecentDateHeader(request);
        hasMatchingMid(media, request);
        isAuthorized(request);
    }

    @Before("target(nl.vpro.media.odi.OdiService) && execution(* *(..)) && args(location, request, ..)")
    public void handleLocation(Location location, HttpServletRequest request) {
        request = patchIE89(request);

        hasRecentDateHeader(request);
        hasMatchingLocation(location, request);
        isAuthorized(request);

    }

    @Before("target(nl.vpro.media.odi.OdiService) && execution(* *(..)) && args(url, request, ..)")
    public void handleUrl(String url, HttpServletRequest request) {
        request = patchIE89(request);

        hasRecentDateHeader(request);
        hasMatchingUrl(url, request);
        isAuthorized(request);
    }


    public void setExpiresInMinutes(int expiresInMinutes) {
        this.expiresInMinutes = expiresInMinutes;
    }

    public void setConfigFolder(String configFolder) {
        this.configFolder = configFolder;
    }

    private void loadAuthorizedClients() {

        Set<OdiClient> newClients = new LinkedHashSet<>();



        final Scanner scanner;
        if (StringUtils.isEmpty(configFolder)) {
            log.warn("No odi.clients folder configured, taking default odi.clients configuration");
            scanner = new Scanner(getClass().getResourceAsStream("/" + CONFIG_FILE));
        } else {
            final File configFile = new File(configFolder, CONFIG_FILE);
            log.info("Loading odi clients from {}", configFile);
            try {
                scanner = new Scanner(configFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Missing ODI config " + configFile, e);
            }

        }
        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.startsWith("#") && !line.isEmpty()) {
                    OdiClient client = extractClientConfig(line);
                    newClients.add(client);
                }
            }
            clients = Collections.unmodifiableSet(newClients);
            log.info("Loaded {} odi clients", clients.size());
        } finally {
            scanner.close();
        }
    }

    private void appendConfigWatcher() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Path path = Paths.get(configFolder);

                WatchService watcher;
                try {
                    watcher = path.getFileSystem().newWatchService();
                    WatchEvent.Kind<?>[] kinds = {StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_MODIFY};

                    path.register(watcher, kinds);
                } catch (IOException e) {
                    log.error("Error starting the odi config watcher on {}", CONFIG_FILE, e);
                    throw new RuntimeException(e);
                }

                while(running) {
                    try {
                        WatchKey key = watcher.take();
                        log.info("Reloading odi clients from {}", configFolder);
                        for (WatchEvent<?> event : key.pollEvents()) {
                            final Path changed = (Path) event.context();
                            if (changed.endsWith(CONFIG_FILE)) {
                                loadAuthorizedClients();
                                break;
                            }
                        }
                        if (!key.reset()) {
                            log.info("Stopped watching as key " + key + " not valid");
                            break;
                        }

                    } catch (Exception e) {
                        log.error("Error reloading odi config from {}. ODI clients might not function properly. Retry after file update", CONFIG_FILE, e);
                    }
                }
            }
        });
    }

    protected OdiClient extractClientConfig(String line) {
        Matcher matcher = CONFIG_PATTERN.matcher(line);
        if(!matcher.find()) {
            throw new RuntimeException("Can't parse \"" + line + "\"");
        }

        String publicKey = matcher.group(1);
        String originsString = matcher.group(4);
        List<String> origins = new ArrayList<>();
        for(String origin : originsString.split(",")) {
            if(!StringUtils.isEmpty(origin)) {
                origins.add(origin.trim());
            }
        }

        String secret = matcher.group(2);
        boolean allowXOrigin = matcher.group(3) != null && "true".equals(matcher.group(3));
        return new OdiClient(
            publicKey,
            origins,
            secret,
            allowXOrigin
        );
    }

    private void hasRecentDateHeader(HttpServletRequest request) {
        String value = request.getHeader(X_NPO_DATE.toLowerCase());
        Date date;
        try {
            date = Util.rfc822(value);
        } catch(Exception e) {
            return;
        }

        boolean expired = Math.abs(date.getTime() - System.currentTimeMillis()) > expiresInMinutes * 60 * 1000;
        if(expired) {
            log.debug("Expired authentication client: {} server: {}", date.getTime(), System.currentTimeMillis());
            throw new NoAccessException("not recent");
        }
    }

    private void hasMatchingMid(MediaObject media, HttpServletRequest request) {
        String header = request.getHeader(X_NPO_MID.toLowerCase());
        if (header == null) {
            throw new NoAccessException("no mid header");
        }
        if (media == null) {
            throw new NoAccessException("no media");
        }
        if (!media.getMid().equals(header)) {
            throw new NoAccessException("mid");
        }
    }

    private void hasMatchingLocation(Location location, HttpServletRequest request) {
        throw new NoAccessException("not implemented");
    }

    private void hasMatchingUrl(String url, HttpServletRequest request) {
        String header = request.getHeader(X_NPO_URL.toLowerCase());
        if (url != null && ! url.equals(header)) {
            throw new NoAccessException("url");
        }
    }

    private void isAuthorized(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);
        if(authorization == null) {
            log.debug("Missing authorization");
            throw new NoAccessException("missing");
        }

        Matcher matcher = HEADER_PATTERN.matcher(authorization);
        if(!matcher.find()) {
            log.debug("Invalid authorization");
            throw new NoAccessException("invalid");
        }

        String publicKey = matcher.group(1);

        boolean hasXOrigin = false;
        String origin = request.getHeader("origin");
        if(origin == null) {
            origin = request.getHeader("X-Origin");
            hasXOrigin = true;
        }

        if(origin == null || origin.length() == 0) {
            log.debug("Missing origin");
            throw new NoAccessException("origin");
        }

        OdiClient client = findMatchingClient(publicKey, origin);
        if(client == null) {
            log.debug("Unauthorised client");
            throw new NoAccessException("client");
        }

        if(hasXOrigin && !client.isAllowXOrigin()) {
            log.debug("X-Origin not allowed for {}", client);
            throw new NoAccessException("xorigin");

        }

        String hmac = matcher.group(2);

        String expectedSecurityHeaders = Util.concatSecurityHeaders(request);
        String expectedHmap = Util.hmacSHA256(client.getSecret(), expectedSecurityHeaders);
        if(! expectedHmap.equals(hmac)) {
            log.debug("Invalid signature " + expectedSecurityHeaders);
            throw new NoAccessException("signature");

        }
    }

    private OdiClient findMatchingClient(String publicKey, String origin) {
        for(OdiClient client : clients) {
            if(client.getPublicKey().equals(publicKey)) {
               if (client.matchesOrigin(origin)) {
                   return client;
               }
            }
        }
        return null;
    }

    private HttpServletRequest patchIE89(HttpServletRequest request) {
        if(!isIE89(request)) {
            return request;
        }

        String ieHeader = request.getParameter("iecomp");
        if (ieHeader == null) {
            return request;
        }
        return new WrappedIE89HttpServletRequest(ieHeader, request);
    }

    private boolean isIE89(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent");
        return userAgent != null && (userAgent.contains("MSIE 9.0")
            || userAgent.contains("MSIE 8.0")
            || userAgent.contains("MSIE 7.0") // Compatibility mode
        );
    }

    public static class NoAccessException extends RuntimeException {
        private final String reason;
        NoAccessException(String reason) {
            super("No access");
            this.reason = reason;
        }
        public String getReason() {
            return reason;
        }
    }

    private static class WrappedIE89HttpServletRequest extends HttpServletRequestWrapper {

        private static final ObjectMapper mapper = new ObjectMapper();

        private final JsonNode param;

        public WrappedIE89HttpServletRequest(String ieParam, HttpServletRequest request) {
            super(request);

            try {
                param = mapper.readTree(Base64.decodeBase64(ieParam));
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String getHeader(String name) {
            switch(name) {
                case X_NPO_DATE:
                    return fromJson(X_NPO_DATE);
                case X_NPO_MID:
                    return fromJson(X_NPO_MID);
                case X_NPO_URL:
                    return fromJson(X_NPO_URL);
                case AUTHORIZATION:
                    return fromJson(AUTHORIZATION);
                default:
                    return super.getHeader(name);
            }
        }

        private String fromJson(String property) {
            JsonNode node = param.get(property);
            return node != null ? node.asText() : null;
        }
    }
}
