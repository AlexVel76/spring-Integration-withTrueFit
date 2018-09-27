package com.myproject.services.export.truefit;

import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFile;
import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFileMethod;

@DelimitedFile(delimiter = "|")
public class TruefitData {
    private String orderDate;
    private String orderID;
    private String customerID;
    private String productID;
    private String productTitle;
    private String hierarchy;
    private String brandName;
    private String brandProductID;
    private String sku;
    private Integer quantity;
    private String price;
    private String size;
    private String size2;
    private String currency;
    private String sellingLocale;
    private String uiLocale;
    private String salesChannel;

    public final String getOrderDate() {
        return orderDate;
    }

    @DelimitedFileMethod(position = 1, name = "OrderDate")
    public final void setOrderDate(final String orderDate) {
        this.orderDate = orderDate;
    }

    public final String getOrderID() {
        return orderID;
    }

    @DelimitedFileMethod(position = 2, name = "OrderID")
    public final void setOrderID(final String orderID) {
        this.orderID = orderID;
    }

    public final String getCustomerID() {
        return customerID;
    }

    @DelimitedFileMethod(position = 3, name = "CustomerID")
    public final void setCustomerID(final String customerID) {
        this.customerID = customerID;
    }

    public final String getProductID() {
        return productID;
    }

    @DelimitedFileMethod(position = 4, name = "ProductID")
    public final void setProductID(final String productID) {
        this.productID = productID;
    }

    public final String getProductTitle() {
        return productTitle;
    }

    @DelimitedFileMethod(position = 5, name = "ProductTitle")
    public final void setProductTitle(final String productTitle) {
        this.productTitle = productTitle;
    }

    public final String getHierarchy() {
        return hierarchy;
    }

    @DelimitedFileMethod(position = 6, name = "Hierarchy")
    public final void setHierarchy(final String hierarchy) {
        this.hierarchy = hierarchy;
    }

    public final String getBrandName() {
        return brandName;
    }

    @DelimitedFileMethod(position = 7, name = "BrandName")
    public final void setBrandName(final String brandName) {
        this.brandName = brandName;
    }

    public final String getBrandProductID() {
        return brandProductID;
    }

    @DelimitedFileMethod(position = 8, name = "BrandProductID")
    public final void setBrandProductID(final String brandProductID) {
        this.brandProductID = brandProductID;
    }

    public final String getSku() {
        return sku;
    }

    @DelimitedFileMethod(position = 9, name = "SKU")
    public final void setSku(final String sku) {
        this.sku = sku;
    }

    public final Integer getQuantity() {
        return quantity;
    }

    @DelimitedFileMethod(position = 10, name = "Quantity")
    public final void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    public final String getPrice() {
        return price;
    }

    @DelimitedFileMethod(position = 11, name = "Price")
    public final void setPrice(final String price) {
        this.price = price;
    }

    public final String getSize() {
        return size;
    }

    @DelimitedFileMethod(position = 12, name = "Size")
    public final void setSize(final String size) {
        this.size = size;
    }

    public String getSize2() {
        return size2;
    }

    @DelimitedFileMethod(position = 13, name = "Size1")
    public void setSize2(final String size2) {
        this.size2 = size2;
    }

    public final String getCurrency() {
        return currency;
    }

    @DelimitedFileMethod(position = 14, name = "Currency")
    public final void setCurrency(final String currency) {
        this.currency = currency;
    }

    public final String getSellingLocale() {
        return sellingLocale;
    }

    @DelimitedFileMethod(position = 15, name = "SellingLocale")
    public final void setSellingLocale(final String sellingLocale) {
        this.sellingLocale = sellingLocale;
    }

    public final String getUiLocale() {
        return uiLocale;
    }

    @DelimitedFileMethod(position = 16, name = "UILocale")
    public final void setUiLocale(final String uiLocale) {
        this.uiLocale = uiLocale;
    }

    public final String getSalesChannel() {
        return salesChannel;
    }

    @DelimitedFileMethod(position = 17, name = "SalesChannel")
    public final void setSalesChannel(final String salesChannel) {
        this.salesChannel = salesChannel;
    }

}
