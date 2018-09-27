package com.myproject.services.export.truefit;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.Message;

public class TrueFitFileNameGenerator implements org.springframework.integration.file.FileNameGenerator {

    private static final String DATE = "%date%";

    private static final String NAME = "%name%";

    @Value("${services.truefit.filename.pattern}")
    private String filenamePatern;

    @Value("${services.truefit.filename.date.format}")
    private String dateFormat;

    @Override
    public String generateFileName(final Message<?> paramMessage) {

        String result = StringUtils.EMPTY;
        TrueFitExportDataEvent event = (TrueFitExportDataEvent) paramMessage.getHeaders().get("event");
        String fileName = event.getFileName();

        result = filenamePatern.replaceFirst(NAME, fileName);
        result = result.replaceFirst(DATE, new SimpleDateFormat(dateFormat).format(new Date()));

        return result;
    }

}
