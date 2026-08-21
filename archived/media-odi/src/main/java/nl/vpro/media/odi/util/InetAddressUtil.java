/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.util;

import java.net.*;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.common.net.InetAddresses;

/**
 * User: rico
 * Date: 04/01/2012
 */
public class InetAddressUtil {
    private static final Pattern PRIVATE_IP_PATTERN = Pattern.compile("(^127\\.)|(^10\\.)|(^172\\.1[6-9]\\.)|(^172\\.2[0-9]\\.)|(^172\\.3[0-1]\\.)|(^192\\.168\\.)");

    private InetAddressUtil() {
    }

    public static boolean isValid(String addr) {
        try {
            InetAddresses.forUriString(addr);
            return true;
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    public static InetAddress getAddress(String addr) {
        return InetAddresses.forUriString(addr);
    }

    public static boolean isPublic(String addr) {
        return !isPrivate(addr);
    }

    public static boolean isPrivate(String addr) {
        InetAddress inetAddress = getAddress(addr);
        if (inetAddress.isSiteLocalAddress() || inetAddress.isAnyLocalAddress() || inetAddress.isLinkLocalAddress() || inetAddress.isLoopbackAddress()) {
            return true;
        }
        return false;
    }

    public static String getClientHost(HttpServletRequest request) {
        String ip = null;
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(forwardedFor)) {
            String[] hosts = forwardedFor.split(",");
            for (String host : hosts) {
                host = host.trim();
                if (InetAddressUtil.isPublic(host)) {
                    ip = host;
                    break;
                }
            }
        }
        if (ip == null) {
            String addr = request.getRemoteAddr().trim();
            if (StringUtils.isNotEmpty(addr) && InetAddressUtil.isPublic(addr)) {
                ip = addr;
            }
        }

        return ip;
    }

}
