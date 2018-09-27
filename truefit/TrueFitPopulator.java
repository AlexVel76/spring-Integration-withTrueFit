package com.myproject.services.export.truefit;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TrueFitPopulator implements Populator<List<Object>, TruefitData> {

    @Value("${service.services.truefit.hierarchy.regexp.from.product.url}")
    private String hierarchyRegexp;

    @Value("${service.services.truefit.output.date.formate}")
    private String dateFormat;

    @Value("${service.services.truefit.product.name.remove.regexp}")
    private String replaceRegexp;

    @Override
    @SuppressWarnings("squid:S1166")
    public void populate(final List<Object> source, final TruefitData target) throws ConversionException {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null. ");
        try {
            target.setOrderID((String) source.get(0));
            target.setOrderDate(formatDate((Date) source.get(1)));
            target.setCustomerID((String) source.get(2));
            target.setProductID((String) source.get(3));
            String productTitle = (String) source.get(4);
            target.setProductTitle(formatString(StringUtils.isBlank(productTitle) ? (String) source.get(17)
                    : productTitle));
            target.setHierarchy(formatString(extractHierarchy((String) source.get(5))));
            target.setBrandName(formatString((String) source.get(6)));
            target.setBrandProductID((String) source.get(7));
            target.setSku((String) source.get(8));
            target.setQuantity((Integer) source.get(9));
            target.setPrice(((Double) source.get(10)).toString());
            target.setSize((String) source.get(11));
            target.setSize2((String) source.get(12));
            target.setCurrency((String) source.get(13));
            target.setSellingLocale((String) source.get(14));
            target.setUiLocale((String) source.get(15));
            target.setSalesChannel((String) source.get(16));
        } catch (Exception e) {
            throw new RuntimeException(e.toString() + " Values: " + StringUtils.join(source, " | "));
        }
    }

    private String formatString(final String text) {
        String result = text;
        if (result != null) {
            result = StringUtils.normalizeSpace(result)
                    .replaceAll(StringEscapeUtils.unescapeJava(replaceRegexp), StringUtils.EMPTY).trim();
        }
        return result;
    }

    private String formatDate(final Date value) {
        return value == null ? StringUtils.EMPTY : new SimpleDateFormat(dateFormat).format(value);
    }

    private String extractHierarchy(final String pageUrl) {
        String result = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(pageUrl)) {
            result = pageUrl.replaceAll(hierarchyRegexp, StringUtils.EMPTY);
        }
        return result;
    }
}
