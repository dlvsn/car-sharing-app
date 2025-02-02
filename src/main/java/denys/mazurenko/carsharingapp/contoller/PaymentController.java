package denys.mazurenko.carsharingapp.contoller;

import denys.mazurenko.carsharingapp.dto.payment.PaymentRequestDto;
import denys.mazurenko.carsharingapp.dto.payment.PaymentResponseDto;
import denys.mazurenko.carsharingapp.dto.payment.PaymentStatusDto;
import denys.mazurenko.carsharingapp.model.User;
import denys.mazurenko.carsharingapp.security.CustomUserDetailsService;
import denys.mazurenko.carsharingapp.service.payment.PaymentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
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
    private final CustomUserDetailsService userDetailsService;
    private final PaymentServiceImpl paymentService;
    private final CustomUserDetailsService customUserDetailsService;

    @Operation(summary = """
            Creates a payment object, calculates the total cost, 
            and returns a session ID and a payment link. 
            Redirects to /success or /cancel endpoints based on the payment outcome.
            """)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDto createPayment(
            Authentication authentication,
            @RequestBody
            @Valid
            PaymentRequestDto requestDto) {
        User user = userDetailsService.getUserFromAuthentication(authentication);
        return paymentService.createPaymentSession(user, requestDto);
    }

    @Operation(summary = """
            Retrieves payment details by its unique identifier for the authenticated user.
            """)
    @GetMapping("/{id}")
    public PaymentResponseDto getPaymentById(
            Authentication authentication,
            @PathVariable
            @Positive Long id) {
        User user = customUserDetailsService.getUserFromAuthentication(authentication);
        return paymentService.findPaymentById(user, id);
    }

    @Operation(summary = """
            Returns the payment status as successful based on the session ID.
            """)
    @GetMapping("/success")
    public PaymentStatusDto getPaymentSuccess(@RequestParam("session_id") String sessionId) {
        return paymentService.getPaymentStatus(sessionId);
    }

    @Operation(summary = """
            Returns the payment status as unsuccessful based on the session ID.
            """)
    @GetMapping("/cancel")
    public PaymentStatusDto cancelPayment(@RequestParam("session_id") String sessionId) {
        return paymentService.getPaymentStatus(sessionId);
    }

    @Operation(summary = """
            Displays the payment history for the authenticated user.
            """)
    @GetMapping
    public List<PaymentResponseDto> getAllPayments(Authentication authentication) {
        User user = customUserDetailsService.getUserFromAuthentication(authentication);
        return paymentService.getPaymentsHistory(user);
    }
}
