package com.yas.payment.controller;

import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.CapturePaymentResponseVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentResponseVm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(value = "/init")
    public InitPaymentResponseVm initPayment(@Valid @RequestBody InitPaymentRequestVm initPaymentRequestVm) {
        return paymentService.initPayment(initPaymentRequestVm);
    }

    @PostMapping(value = "/capture")
    public CapturePaymentResponseVm capturePayment(
        @Valid @RequestBody CapturePaymentRequestVm capturePaymentRequestVm
    ) {
        return paymentService.capturePayment(capturePaymentRequestVm);
    }

    @GetMapping(value = "/cancel")
    public ResponseEntity<String> cancelPayment() {
        return ResponseEntity.ok("Payment cancelled");
    }
}
