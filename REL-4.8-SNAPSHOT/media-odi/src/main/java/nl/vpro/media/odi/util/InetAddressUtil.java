/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.util;

import sun.net.util.IPAddressUtil;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * User: rico
 * Date: 04/01/2012
 */
public class InetAddressUtil {
    private static final Pattern PRIVATE_IP_PATTERN = Pattern.compile("(^127\\.)|(^10\\.)|(^172\\.1[6-9]\\.)|(^172\\.2[0-9]\\.)|(^172\\.3[0-1]\\.)|(^192\\.168\\.)");

    public static boolean isValid(String addr) {
        return (IPAddressUtil.isIPv4LiteralAddress(addr) || IPAddressUtil.isIPv6LiteralAddress(addr));
    }

    public static InetAddress getAddress(String addr) {
        InetAddress inetAddress = null;
        if (IPAddressUtil.isIPv4LiteralAddress(addr)) {
            try {
                inetAddress = Inet4Address.getByAddress(IPAddressUtil.textToNumericFormatV4(addr));
            } catch (UnknownHostException e) {
            }
        }

        if (IPAddressUtil.isIPv6LiteralAddress(addr)) {
            try {
                inetAddress = InetAddress.getByAddress(IPAddressUtil.textToNumericFormatV6(addr));
            } catch (UnknownHostException e) {
            }
        }

        return inetAddress;
    }

    public static boolean isPublic(String addr) {
        return !isPrivate(addr);
    }

    public static boolean isPrivate(String addr) {
        InetAddress inetAddress = getAddress(addr);
        if (inetAddress != null) {
            if (inetAddress.isAnyLocalAddress() || inetAddress.isLinkLocalAddress() || inetAddress.isLoopbackAddress()) {
                return true;
            }
            if (inetAddress instanceof Inet4Address) {
                if (PRIVATE_IP_PATTERN.matcher(inetAddress.getHostAddress()).find()) {
                    return true;
                }
            }
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
