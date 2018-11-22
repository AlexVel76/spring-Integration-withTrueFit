package com.site2.services.export.googlelocalad.integration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.file.filters.FileListFilter;

import com.jcraft.jsch.ChannelSftp;

public class LatestFileFilter<F> implements FileListFilter<F> {

    private final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    @Value("${services.googlelocalad.ftp.filename.group.site.site1:site1}")
    private String site1;
    @Value("${services.googlelocalad.ftp.filename.group.site.site2:site2}")
    private String site2;
    private String patternName;//((LIA_products_)(Site2|Site1)_([0-9]{4})(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])((0[1-9]|[0-9][0-9])|())(.csv))

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(final String patternName) {
        this.patternName = patternName;
    }

    @Override
    public List<F> filterFiles(final F[] files) {
        List<F> result = new ArrayList<>();

        int site1LastFileIndex = -1;
        int site2LastFileIndex = -1;

        String site1LastFileName = StringUtils.EMPTY;
        String site2LastFileName = StringUtils.EMPTY;
        int currentIndex = 0;
        for (F file : files) {
            if (file instanceof ChannelSftp.LsEntry) {
                String currentName = ((ChannelSftp.LsEntry) file).getFilename();

                if (currentName.contains(site1)
                        && currentElementIsLatestAndToday(site1LastFileName, currentName)) {
                    site1LastFileName = currentName;
                    site1LastFileIndex = currentIndex;
                }

                if (currentName.contains(site2) && currentElementIsLatestAndToday(site2LastFileName, currentName)) {
                    site2LastFileName = currentName;
                    site2LastFileIndex = currentIndex;
                }
            }
            currentIndex++;
        }

        if (site1LastFileIndex >= 0) {
            result.add(files[site1LastFileIndex]);
        }

        if (site2LastFileIndex >= 0) {
            result.add(files[site2LastFileIndex]);
        }

        return result;
    }

    private boolean currentElementIsLatestAndToday(final String lastFileName, final String currentName) {
        boolean result = false;
        if (currentFileIsToday(currentName)) {
            result = StringUtils.isEmpty(lastFileName) ||
                    getDateAndSequenceFromName(currentName) > getDateAndSequenceFromName(lastFileName);
        }
        return result;
    }

    private boolean currentFileIsToday(final String currentName) {
        return getDateFromName(currentName).equals(format.format(new Date()));
    }

    private long getDateAndSequenceFromName(final String currentName) {
        long result = 0;
        Pattern pattern = Pattern.compile(patternName);
        Matcher matcher = pattern.matcher(currentName);
        if (matcher.matches())
        {
            String sequence = StringUtils.isBlank(matcher.group(7)) ? "00" : matcher.group(7);

            try {
                result = Long.parseLong(String.format("%s%s", getDateFromName(currentName), sequence));
            } catch (NumberFormatException e) {
                result = 0;
            }
        }
        return result;
    }

    private String getDateFromName(final String currentName) {
        String result = "";
        Pattern pattern = Pattern.compile(patternName);
        Matcher matcher = pattern.matcher(currentName);
        if (matcher.matches()) {
            String year = matcher.group(4);
            String month = matcher.group(5);
            String day = matcher.group(6);

            result = String.format("%s%s%s", year, month, day);
        }
        return result;
    }

}
