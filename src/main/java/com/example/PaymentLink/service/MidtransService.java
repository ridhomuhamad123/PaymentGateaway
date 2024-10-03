package com.example.PaymentLink.service;

import com.example.PaymentLink.model.DynamicAmount;
import com.example.PaymentLink.model.ItemDetail;
import com.example.PaymentLink.model.PaymentLink;
import com.example.PaymentLink.repository.PaymentLinkRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MidtransService {

    private final RestTemplate restTemplate;
    private final PaymentLinkRepository paymentLinkRepository;

    @Value("${midtrans.api-url-payment-link}")
    private String apiUrlPaymentLink;  // URL untuk payment link

    @Value("${midtrans.server-key}")
    private String serverKey;

    private static final Logger logger = LoggerFactory.getLogger(MidtransService.class);

    public MidtransService(RestTemplate restTemplate, PaymentLinkRepository paymentLinkRepository) {
        this.restTemplate = restTemplate;
        this.paymentLinkRepository = paymentLinkRepository;
    }

    public String createPaymentLink(Map<String, Object> request) {
        // Simpan data ke database
        PaymentLink paymentLink = new PaymentLink();
        Map<String, Object> transactionDetails = (Map<String, Object>) request.get("transaction_details");
        Map<String, Object> customerDetails = (Map<String, Object>) request.get("customer_details");
        Map<String, Object> expiryDetails = (Map<String, Object>) request.get("expiry"); // Ambil expiry details
        Map<String, Object> dynamicAmount = (Map<String, Object>) request.get("dynamic_amount"); // Ambil dynamic amount details

        // Assign data dari request ke entity PaymentLink
        paymentLink.setOrderId((String) transactionDetails.get("order_id"));
        paymentLink.setGrossAmount((Integer) transactionDetails.get("gross_amount"));
        paymentLink.setCustomerRequired((Boolean) request.get("customer_required"));
        paymentLink.setBank((String) ((Map<String, Object>) request.get("credit_card")).get("bank"));
        paymentLink.setSecure((Boolean) ((Map<String, Object>) request.get("credit_card")).get("secure"));
        paymentLink.setFirstName((String) customerDetails.get("first_name"));
        paymentLink.setLastName((String) customerDetails.get("last_name"));
        paymentLink.setEmail((String) customerDetails.get("email"));
        paymentLink.setPhone((String) customerDetails.get("phone"));
        paymentLink.setNotes((String) customerDetails.get("notes"));
        paymentLink.setTitle((String) request.get("title"));
        paymentLink.setCustomField1((String) request.get("custom_field1"));
        paymentLink.setCustomField2((String) request.get("custom_field2"));
        paymentLink.setCustomField3((String) request.get("custom_field3"));
        paymentLink.setCallbackUrl((String) ((Map<String, Object>) request.get("callbacks")).get("finish"));

        // Set expiry details
        paymentLink.setStartTime((String) expiryDetails.get("start_time"));
        paymentLink.setDuration((Integer) expiryDetails.get("duration"));
        paymentLink.setUnit((String) expiryDetails.get("unit"));
        paymentLink.setUsageLimit((Integer) request.get("usage_limit"));

        // Set dynamic amount details
        if (dynamicAmount != null) {
            DynamicAmount dynamicAmountEntity = new DynamicAmount();
            dynamicAmountEntity.setMinAmount((Integer) dynamicAmount.get("min_amount"));
            dynamicAmountEntity.setMaxAmount((Integer) dynamicAmount.get("max_amount"));
            dynamicAmountEntity.setPresetAmount((Integer) dynamicAmount.get("preset_amount"));
            paymentLink.setDynamicAmount(dynamicAmountEntity); // Set dynamicAmount entity ke PaymentLink
        }

        // Map item_details dari request ke PaymentLink
        List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("item_details");
        List<ItemDetail> itemDetails = new ArrayList<>();

        for (Map<String, Object> item : items) {
            ItemDetail itemDetail = new ItemDetail();
            itemDetail.setName((String) item.get("name"));
            itemDetail.setPrice((Integer) item.get("price"));
            itemDetail.setQuantity((Integer) item.get("quantity"));
            itemDetail.setBrand((String) item.get("brand"));
            itemDetail.setCategory((String) item.get("category"));
            itemDetail.setMerchantName((String) item.get("merchant_name"));
            itemDetail.setPaymentLink(paymentLink); // Set relasi dengan PaymentLink
            itemDetails.add(itemDetail);
        }

        paymentLink.setItemDetails(itemDetails);


        // Address handling
        String address = customerDetails.get("address") != null ? (String) customerDetails.get("address") : "";
        String city = customerDetails.get("city") != null ? (String) customerDetails.get("city") : "";
        String postalCode = customerDetails.get("postal_code") != null ? (String) customerDetails.get("postal_code") : "";
        String country = customerDetails.get("country") != null ? (String) customerDetails.get("country") : "";

        String fullAddress = String.join(", ", address, city, postalCode, country);
        paymentLink.setCustomerAddress(fullAddress);


        // Menambahkan data 'payment_link_enable_payment' ke dalam request
        List<String> enablePayments = (List<String>) request.get("payment_link_enabled_payments");
        if (enablePayments != null && !enablePayments.isEmpty()) {
            paymentLink.setEnabledPayments(enablePayments);
        }

        try {
            // Simpan ke database
            paymentLinkRepository.save(paymentLink);
            logger.info("PaymentLink {} berhasil disimpan ke database.", paymentLink.getOrderId());

            // Setup headers untuk panggilan API Midtrans
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((serverKey + ":").getBytes()));

            // Create request entity
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            // Call Midtrans API
            ResponseEntity<Map> response = restTemplate.exchange(apiUrlPaymentLink, HttpMethod.POST, entity, Map.class);

            // Check if response is successful
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                String paymentLinkUrl = (String) responseBody.get("payment_url");

                logger.info("PaymentLink berhasil dibuat dengan URL: {}", paymentLinkUrl);
                return paymentLinkUrl;
            } else {
                logger.error("Gagal membuat PaymentLink. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to create payment link: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Terjadi kesalahan saat membuat PaymentLink: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Gagal membuat PaymentLink", e);
        }
    }
}
