package com.touchspring.ailge.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringToDateConverter implements Converter<String, Date> {


    private final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat yyyyMMddHHmm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final SimpleDateFormat yyyyMMddHHmmUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
    private final SimpleDateFormat yyyyMMddTHHmmssSSSS =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private final static int lengYyyyMMdd = 10;
    private final static int lengYyyyMMddHHmmss = 19;
    private final static int lengyyyyMMddHHmm = 16;
    private final static int lengyyyyMMddHHmmUTC = 24;
    private final static int lengyyyyMMddTHHmmssSSSS = 29;

    @Override
    public Date convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        source = source.trim();
        try {
            if(source.length() == lengYyyyMMdd) {
                try {
                    return yyyyMMdd.parse(source);
                } catch (ParseException e) {
                    e.printStackTrace();

                }
            } else if(source.length() == lengYyyyMMddHHmmss) {
                try {
                    return yyyyMMddHHmmss.parse(source);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if(source.length() == lengyyyyMMddHHmm) {
                try {
                    return yyyyMMddHHmm.parse(source);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (source.length() == lengyyyyMMddHHmmUTC){
                source = source.replace("Z", " UTC");
                try {
                    return yyyyMMddHHmmUTC.parse(source);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (source.length() ==lengyyyyMMddTHHmmssSSSS) {
                source = source.replace("Z", " UTC");
                try {
                    return yyyyMMddTHHmmssSSSS.parse(source);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("parser %s to Date fail", source));
        }
        throw new RuntimeException(String.format("parser %s to Date fail", source));
    }
}