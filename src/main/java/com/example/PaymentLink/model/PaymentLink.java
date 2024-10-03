package com.example.PaymentLink.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class PaymentLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private Integer grossAmount;
    private Boolean customerRequired;
    private String bank;
    private Boolean secure;
    private Integer usageLimit;
    private String startTime;
    private Integer duration;
    private String unit;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String notes;
    private String title;
    private String customField1;
    private String customField2;
    private String customField3;
    private String callbackUrl;

    @ElementCollection
    @CollectionTable(name = "payment_link_enabled_payments", joinColumns = @JoinColumn(name = "payment_link_id"))
    private List<String> enabledPayments;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "paymentLink")
    private List<ItemDetail> itemDetails;

    @Embedded // Gunakan @OneToOne jika DynamicAmount adalah entitas
    private DynamicAmount dynamicAmount;

    // Anda bisa menggunakan satu field saja untuk menyimpan alamat penuh
    private String customerAddress;

    // Jika ingin mempertahankan alamat terpisah, gunakan ini:
    // private String customerCity;
    // private String customerPostalCode;
    // private String customerCountry;

    // Getter dan Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(Integer grossAmount) {
        this.grossAmount = grossAmount;
    }

    public Boolean getCustomerRequired() {
        return customerRequired;
    }

    public void setCustomerRequired(Boolean customerRequired) {
        this.customerRequired = customerRequired;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCustomField1() {
        return customField1;
    }

    public void setCustomField1(String customField1) {
        this.customField1 = customField1;
    }

    public String getCustomField2() {
        return customField2;
    }

    public void setCustomField2(String customField2) {
        this.customField2 = customField2;
    }

    public String getCustomField3() {
        return customField3;
    }

    public void setCustomField3(String customField3) {
        this.customField3 = customField3;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public List<String> getEnabledPayments() {
        return enabledPayments;
    }

    public void setEnabledPayments(List<String> enabledPayments) {
        this.enabledPayments = enabledPayments;
    }

    public List<ItemDetail> getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(List<ItemDetail> itemDetails) {
        this.itemDetails = itemDetails;
    }

    public DynamicAmount getDynamicAmount() {
        return dynamicAmount;
    }

    public void setDynamicAmount(DynamicAmount dynamicAmount) {
        this.dynamicAmount = dynamicAmount;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }
}
