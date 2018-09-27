package com.myproject.services.export.truefit;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

@Component("trueFitDataProcessorService")
public class TrueFitDataProcessorService {

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Resource(name = "trueFitConverter")
    private Converter<List<Object>, TruefitData> trueFitConverter;

    @Resource(name = "exportConvertObjectsToCSV")
    private Converter<List<TruefitData>, String> exportConvertObjectsToCSV;

    @SuppressWarnings("unchecked")
    public Collection<List<Object>> getTrueFit(final Message<TrueFitExportDataEvent> message) throws ParseException {
        String locale = message.getPayload().getLocale().getLanguage();
        String query = "select {o.code}, {o.modifiedtime}, {u.customerID}, {mp.webid} as productid, {mp.productTitle["
                + locale
                + "]}, {oe.productPageUrl}, "
                + " {mp.productBrand} as brandName, {mp.pmmCode} as BrandProductId,{mvp.pmmCode} as sku, "
                + " {oe.quantity}, {oe.basePrice}, {mvp.size1}, {mvp.size2}, {c.isocode}, 'US' as sellingLocale, '"
                + locale
                + "_CA' as uiLocale, 'Online' as salesChannel,  {mp.name["
                + locale
                + "]} from {Order as o join OrderStatus as os on {os.pk}={o.status} and ({os.code}='ORDER_PROCESSING' or {os.code}='COMPLETED' "
                + " or {os.code}='ORDER_SENT_TO_OMS' or {os.code}='ORDER_PAID' or {os.code}='ORDER_ON_HOLD' or {os.code}='COMPLETING')  AND {o.originSite}=?originSite  "
                + " join Language as l on {l.pk}={o.language} AND {l.isocode}=?isoCode "
                + " AND {o.modifiedtime} >= ?startDate AND {o.modifiedtime} < ?endDate "
                + " join OrderEntry as oe ON {oe.order}={o.pk} " + " join Customer as u ON {u.pk}={o.user} "
                + " join MyprojectVariantProduct AS mvp ON {mvp.pk}={oe.product} "
                + " join MyprojectProduct AS mp ON {mp.pk}={mvp.baseProduct} "
                + " join Currency AS c ON {c.pk}={o.currency} " + " } ";

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
        searchQuery.addQueryParameter("startDate", message.getPayload().getStartDate());
        searchQuery.addQueryParameter("endDate", message.getPayload().getEndDate());
        searchQuery.addQueryParameter("originSite", message.getPayload().getOriginSite());
        searchQuery.addQueryParameter("isoCode", message.getPayload().getLocale().getLanguage());

        searchQuery.setResultClassList(ImmutableList.of(String.class, Date.class, String.class, String.class,
                String.class, String.class, String.class, String.class, String.class, Integer.class, Double.class,
                String.class, String.class, String.class, String.class, String.class, String.class, String.class));

        final SearchResult<List<Object>> searchResult = flexibleSearchService.search(searchQuery);

        return searchResult.getResult();
    }

    public List<TruefitData> convertItems(final Message<Collection<List<Object>>> message) {
        return Converters.convertAll(message.getPayload(), trueFitConverter);
    }

    public Object convertToCSV(final List<TruefitData> records) {
        return exportConvertObjectsToCSV.convert(records);
    }

}
