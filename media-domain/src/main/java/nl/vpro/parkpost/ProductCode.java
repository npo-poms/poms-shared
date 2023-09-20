/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.parkpost;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.Schedule;

/**
 * See <a href="https://jira.vpro.nl/browse/MSE-1324">JIRA</a>
 * <p>
 * De productcode van de promo is als volgt opgebouwd: [Z][Y][ddmm][CD][TITEL]
 * <p>
 * Waarbij:
 * [Z]=Zender waar het programma wordt uitgezonden (1,2,3 of Z]
 * [Y]=Type (P,T, etc.) waarbij alleen de P relevant is voor POMS, want dat zijn de promo's
 * [ddmm]=dag en maand van de uitzending van het programma
 * [CD]=Versie MO=morgen, VD=Vanavond, MA, DI, WO, DO, VR, ZA, ZO
 * [TITEL]=deel van de titel van het programma
 *
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
public class ProductCode  implements Serializable {

    @Serial
    private static final long serialVersionUID = 2625635499389550495L;

    public enum Type {
        P("PROMO"),
        T("TRAILER"),
        A("AANKONDIGING");

        @Getter
        private final String string;

        Type(String string) {
            this.string = string;
        }

    }

    public static class ParseException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = -5852212513798937395L;

        public ParseException(String s) {
            super(s);
        }
    }

    private static final Pattern PATTERN = Pattern.compile("^([123Z])(\\w)(\\d\\d)(\\d\\d)(.{2,2})(.*)$");

    private String code;

    private Character channelCode;

    private Channel channel;

    private Character typeCode;

    private Type type;

    private Date date;

    private String versionCode;

    private String title;

    private ProductCode() {
    }

    public static ProductCode parse(String code) {
        Matcher matcher = PATTERN.matcher(code);
        if(!matcher.find()) {
            throw new ParseException("Could not parse productcode: " + code);
        }

        ProductCode result = new ProductCode();
        result.code = code;

        char channel = matcher.group(1).charAt(0);
        result.channelCode = channel;
        result.channel = parseChannel(channel);

        char type = matcher.group(2).charAt(0);
        result.typeCode = type;
        result.type = parseType(type);

        int day = Integer.parseInt(matcher.group(3));
        int month = Integer.parseInt(matcher.group(4));
        result.date = parseDate(day, month);

        result.versionCode = matcher.group(5);
        result.title = matcher.group(6);
        return result;
    }

    public Channel getChannel() {
        return channel;
    }

    public Type getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public String getTitle() {
        return title;
    }

    private static Type parseType(char type) {
        return Type.valueOf("" + type);
    }

    private static Channel parseChannel(char channel) {
        switch(channel) {
            case '1': return Channel.NED1;
            case '2': return Channel.NED2;
            case '3': return Channel.NED3;
            case 'Z': return null; // Zapp/Zappelin; not a real channel...
            default: throw new IllegalArgumentException("Unexpected Channel character: " + channel + " expected one of 1/2/3");
        }
    }

    private static Date parseDate(int day, int month) {
        LocalDate now = LocalDate.now(Schedule.ZONE_ID);
        int year = now.getMonthValue() >= 10 && month <= 6 ? now.getYear() + 1 : now.getYear();

        Instant result = LocalDate.of(year, month, day).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant();
        return Date.from(result);
    }
}
