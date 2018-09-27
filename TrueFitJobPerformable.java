package com.myproject.services.cronjob;

import com.myproject.core.constants.MyprojectCoreConstants;
import com.myproject.services.configuration.MyprojectConfigurationService;
import com.myproject.services.export.truefit.TrueFitExportDataEvent;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.event.EventService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Locale;

public class TrueFitJobPerformable extends AbstractJobPerformable<CronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TrueFitJobPerformable.class);

    private String filenameMyprojecten;
    private String filenameMyprojectfr;
    private String filenameSite2en;
    private String filenameSite2fr;

    @Autowired
    private MyprojectConfigurationService myprojectConfigurationService;

    @Autowired
    private EventService eventService;

    @Override
    public PerformResult perform(final CronJobModel cronJobModel) {

        LOG.info("Start trueFit export.");

        eventService.publishEvent(getEvent(filenameMyprojecten, Locale.ENGLISH, MyprojectCoreConstants.Myproject_CLIENT_SITE));

        eventService.publishEvent(getEvent(filenameMyprojectfr, Locale.FRENCH, MyprojectCoreConstants.Myproject_CLIENT_SITE));

        eventService.publishEvent(getEvent(filenameSite2en, Locale.ENGLISH,
                MyprojectCoreConstants.SITE2_CLIENT_SITE));

        eventService
                .publishEvent(getEvent(filenameSite2fr, Locale.FRENCH, MyprojectCoreConstants.SITE2_CLIENT_SITE));

        LOG.info("End trueFit export.");

        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private TrueFitExportDataEvent getEvent(final String filename, final Locale locale, final String originSite) {
        final TrueFitExportDataEvent myprojectenEvent = new TrueFitExportDataEvent();
        myprojectenEvent.setFileName(filename);
        myprojectenEvent.setLocale(locale);
        myprojectenEvent.setOriginSite(originSite);
        Date curDate = new Date();
        myprojectenEvent.setStartDate(getStartDate(curDate));
        myprojectenEvent.setEndDate(curDate);
        return myprojectenEvent;
    }

    private Date getStartDate(final Date curDate) {
        return DateUtils.addDays(curDate, -myprojectConfigurationService.getTrueFitExportPeriodInDays());
    }

    public String getFilenameMyprojecten() {
        return filenameMyprojecten;
    }

    public void setFilenameMyprojecten(final String filenameMyprojecten) {
        this.filenameMyprojecten = filenameMyprojecten;
    }

    public String getFilenameMyprojectfr() {
        return filenameMyprojectfr;
    }

    public void setFilenameMyprojectfr(final String filenameMyprojectfr) {
        this.filenameMyprojectfr = filenameMyprojectfr;
    }

    public String getFilenameSite2en() {
        return filenameSite2en;
    }

    public void setFilenameSite2en(final String filenameSite2en) {
        this.filenameSite2en = filenameSite2en;
    }

    public String getFilenameSite2fr() {
        return filenameSite2fr;
    }

    public void setFilenameSite2fr(final String filenameSite2fr) {
        this.filenameSite2fr = filenameSite2fr;
    }
}