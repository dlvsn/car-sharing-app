package denys.mazurenko.carsharingapp.contoller;

import denys.mazurenko.carsharingapp.dto.payment.PaymentRequestDto;
import denys.mazurenko.carsharingapp.dto.payment.PaymentResponseDto;
import denys.mazurenko.carsharingapp.dto.payment.PaymentStatusDto;
import denys.mazurenko.carsharingapp.service.payment.PaymentServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentServiceImpl paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDto createPayment(
            Authentication authentication,
            @RequestBody
            @Valid
            PaymentRequestDto requestDto) {
        return paymentService.createPaymentSession(authentication, requestDto);
    }

    @GetMapping("/{id}")
    public PaymentResponseDto getPaymentById(
            Authentication authentication,
            @PathVariable
            @Positive Long id) {
        return paymentService.findPaymentById(authentication, id);
    }

    @GetMapping("/success")
    public PaymentStatusDto getPaymentStatus(@RequestParam("session_id") String sessionId) {
        return paymentService.paymentSuccess(sessionId);
    }

    @GetMapping
    public List<PaymentResponseDto> getAllPayments(Authentication authentication) {
        return paymentService.getPaymentsHistory(authentication);
    }
}
