package com.site1.services.export.googlelocalad.integration;

import com.google.common.base.Splitter;
import com.site1.core.dao.FileNameInfoDao;
import com.site1.core.enums.FileNameBusinessArea;
import com.site1.core.enums.SiteType;
import com.site1.core.model.FileNameInfoModel;
import com.site1.services.export.googlelocalad.data.GoogleLocalAdData;
import com.site1.services.export.googlelocalad.marshaller.GoogleLocalAdDataMarshaller;
import com.site1.services.export.googlelocalad.service.GoogleLocalAdServiceImpl;
import com.site1.services.session.Site1SessionService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHeaders;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilder;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;

public class GooglelocaladServiceEndpoint {

    public static final String CURRENT_DATE_TAG = "$currentDate$";
    private static final Logger LOG = LoggerFactory.getLogger(GooglelocaladServiceEndpoint.class);
    private static final String SEQUENCE_TAG = "$sequence$";
    @Value("${services.googlelocalad.source.delimiter:\r\n}")
    private String delimiter;

    @Value("${services.googlelocalad.local.processingdir}")
    private String processingDir;

    @Value("${services.googlelocalad.ftp.filename.group.site.site1}")
    private String site1Site;

    @Value("${services.googlelocalad.out.filename.site1.pattern}")
    private String filenameSite1;

    @Value("${services.googlelocalad.out.filename.site2.pattern}")
    private String fileNameSite2;

    @Value("${services.googlelocalad.delta.out.filename.site1.pattern:LIA_products_inventory_delta_Site2_$currentDate$$sequence$.txt}")
    private String deltaFilenameSite1;

    @Value("${services.googlelocalad.delta.out.filename.site2.pattern}")
    private String deltaFilenameSite2;

    @Value("${services.googlelocalad.out.filename.currentDateFormat}")
    private String currentDateFormat;

    @Value("${services.googlelocalad.out.sftp.host}")
    private String ftpOutHost;

    @Value("${services.googlelocalad.out.ftp.dir}")
    private String ftpOutFullFolder;

    @Value("${services.googlelocalad.delta.out.ftp.dir}")
    private String ftpOutDeltaFolder;

    @Value("${services.googlelocalad.out.filename.use.same.name.as.inbound}")
    private boolean useInboundName;

    @Value("${service.googlelocalad.out.file.extension:txt}")
    private String fileExtension;

    @Value("${service.googlelocalad.out.file.name.replace.regex:[^.]*$}")
    private String fileReplaceExtentionRegex;

    @Autowired
    private Site1SessionService site1SessionService;

    @Autowired
    private GoogleLocalAdServiceImpl googleLocalAdServiceImpl;

    @Autowired
    private GoogleLocalAdDataMarshaller googleLocalAdDataMarshaller;

    @Autowired
    private ModelService modelService;

    @Autowired
    private FileNameInfoDao fileNameInfoDao;

    @ServiceActivator
    public Message<File> transformPlaineListToFile(final Message<String> message) throws IOException {
        final Splitter splitter = Splitter.on(StringEscapeUtils.unescapeJava(delimiter)).trimResults()
                .omitEmptyStrings();
        Iterable<String> ids = splitter.split(message.getPayload());

        List<String> idList = new ArrayList<String>();
        Iterator<String> iterator = ids.iterator();
        while (iterator.hasNext()) {
            idList.add(iterator.next());
        }

        return createFileMessage(message.getHeaders(), idList);
    }

    @ServiceActivator
    public Message<File> transformListToFile(final Message<List<String>> message) throws IOException {
        return createFileMessage(message.getHeaders(), message.getPayload());
    }

    private Message<File> createFileMessage(final MessageHeaders messageHeaders,
            final List<String> idList) throws IOException {

        LOG.info("Input Message:" + messageHeaders.toString());

        FileNameInfoModel fileNameInfoModel = modelService.create(FileNameInfoModel.class);
        fileNameInfoModel.setAreaCode(FileNameBusinessArea.GOOGLELIA);
        fileNameInfoModel.setFolder(ftpOutHost + " : " + (messageHeaders.containsKey(GoogleLiaConstants.IS_DELTA_FEED)
                ? ftpOutDeltaFolder : ftpOutFullFolder));

        Files.createDirectories(Paths.get(processingDir));
        String outputFileName = getOutputFileName(messageHeaders, fileNameInfoModel);
        Path tmpFilePath = Paths.get(processingDir, outputFileName);

        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(tmpFilePath, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING), StandardCharsets.UTF_8)) {
            googleLocalAdDataMarshaller.marshal(writer, getProducts(idList));
        } catch (IOException e) {
            LOG.error("Error while writing to file:" + tmpFilePath, e);
        }

        Message<File> resultFileMessage = MessageBuilder
                .withPayload(tmpFilePath.toFile())
                .setHeader(GoogleLiaConstants.FILE_NAME, outputFileName)
                .setHeader(GoogleLiaConstants.IS_DELTA_FEED,
                        messageHeaders.containsKey(GoogleLiaConstants.IS_DELTA_FEED))
                .setHeader(GoogleLiaConstants.FILE_INFO_CONTEXT, fileNameInfoModel)
                .build();


        modelService.save(fileNameInfoModel);

        LOG.info("Output Message:" + resultFileMessage.toString());
        return resultFileMessage;
    }

    private String getOutputFileName(final MessageHeaders messageHeaders, final FileNameInfoModel fileNameInfoModel) {
        String result = "";

        if (messageHeaders.containsKey(GoogleLiaConstants.IS_DELTA_FEED)) {
            result = messageHeaders.get(GoogleLiaConstants.SITE_TYPE).equals(SiteType.MARKS) ? deltaFilenameSite1 :
                    deltaFilenameSite2;
        } else {
            String inboundFileName = (String) messageHeaders.get(GoogleLiaConstants.FILE_NAME);
            result = useInboundName ? inboundFileName.replaceFirst(fileReplaceExtentionRegex, fileExtension) : generateNewFileName(inboundFileName);
        }
        fileNameInfoModel.setPatternName(result);

        if (result.contains(CURRENT_DATE_TAG)) {
            result = result.replace(CURRENT_DATE_TAG, new SimpleDateFormat
                    (currentDateFormat).format(new Date()));

            if (result.contains(SEQUENCE_TAG)) {
                Integer lastSequenceNumber = fileNameInfoDao.findLastSequenceNumber(fileNameInfoModel.getPatternName());
                lastSequenceNumber++;
                Integer newSeqNumber = lastSequenceNumber;
                result = result.replace(SEQUENCE_TAG, String.format("%02d", newSeqNumber));
                fileNameInfoModel.setSequenceNumber(newSeqNumber);
            }
        }

        fileNameInfoModel.setGeneratedName(result);

        return result;
    }


    private String generateNewFileName(final String oldFileName) {
        return oldFileName.contains(site1Site) ? filenameSite1 : fileNameSite2;
    }

    @SuppressWarnings("unchecked")
    private List<GoogleLocalAdData> getProducts(final List<String> idList) {
        return (List<GoogleLocalAdData>) site1SessionService
                .executeAsConsistencyCheckerWithResult(new SessionExecutionBody() {
                    @Override
                    public List<GoogleLocalAdData> execute() {
                        return googleLocalAdServiceImpl.getData(idList);
                    }
                });
    }

}
