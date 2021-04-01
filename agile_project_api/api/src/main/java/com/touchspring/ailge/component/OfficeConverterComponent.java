package com.touchspring.ailge.component;

import lombok.extern.slf4j.Slf4j;
import org.jodconverter.DocumentConverter;
import org.jodconverter.office.OfficeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class OfficeConverterComponent {


    @Autowired
    private DocumentConverter converter;  //用于转换

    private final static String KEY = "TO_CONVERTER";

    @Async
   public void converterToPdf(String sourceFile, String toConverterFile) {
        try {
            this.toConverter(sourceFile,toConverterFile);
        } catch (OfficeException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    private void toConverter(String sourceFile, String toConverterFile) throws OfficeException {
        converter.convert(new File(sourceFile)).to(new File(toConverterFile)).execute();
    }
}
