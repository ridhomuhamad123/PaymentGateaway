package com.example.PaymentLink.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class DynamicAmount {

    private Integer minAmount;
    private Integer maxAmount;
    private Integer presetAmount;

    // Getter dan Setter

    public Integer getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Integer minAmount) {
        this.minAmount = minAmount;
    }

    public Integer getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Integer getPresetAmount() {
        return presetAmount;
    }

    public void setPresetAmount(Integer presetAmount) {
        this.presetAmount = presetAmount;
    }
}
