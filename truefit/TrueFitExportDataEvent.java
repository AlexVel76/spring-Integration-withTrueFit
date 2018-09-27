package com.myproject.services.export.truefit;

import java.util.Date;
import java.util.Locale;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;

public class TrueFitExportDataEvent extends AbstractEvent {

    private Date startDate;
    private Date endDate;
    private Locale locale;
    private String originSite;
    private String fileName;

    public final Locale getLocale() {
        return locale;
    }

    public final void setLocale(final Locale locale) {
        this.locale = locale;
    }

    public final String getOriginSite() {
        return originSite;
    }

    public final void setOriginSite(final String originSite) {
        this.originSite = originSite;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(final Date endDate) {
        this.endDate = endDate;
    }

}
