package denys.mazurenko.easydrive.contoller;

import static denys.mazurenko.easydrive.util.DatabaseTestScripts.DELETE_CARS_SQL;
import static denys.mazurenko.easydrive.util.DatabaseTestScripts.DELETE_PAYMENTS_SQL;
import static denys.mazurenko.easydrive.util.DatabaseTestScripts.DELETE_RENTALS_SQL;
import static denys.mazurenko.easydrive.util.DatabaseTestScripts.DELETE_ROLES_USERS_SQL;
import static denys.mazurenko.easydrive.util.DatabaseTestScripts.DELETE_USERS_SQL;
import static denys.mazurenko.easydrive.util.DatabaseTestScripts.INSERT_CARS_SQL;
import static denys.mazurenko.easydrive.util.DatabaseTestScripts.INSERT_PAYMENTS_SQL;
import static denys.mazurenko.easydrive.util.DatabaseTestScripts.INSERT_RENTALS_SQL;
import static denys.mazurenko.easydrive.util.DatabaseTestScripts.INSERT_USERS_SQL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import denys.mazurenko.easydrive.dto.payment.PaymentRequestDto;
import denys.mazurenko.easydrive.dto.payment.PaymentResponseDto;
import denys.mazurenko.easydrive.util.TestObjectBuilder;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
        INSERT_USERS_SQL,
        INSERT_CARS_SQL,
        INSERT_RENTALS_SQL,
        INSERT_PAYMENTS_SQL
}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = {
        DELETE_PAYMENTS_SQL,
        DELETE_RENTALS_SQL,
        DELETE_ROLES_USERS_SQL,
        DELETE_USERS_SQL,
        DELETE_CARS_SQL
}, executionPhase = AFTER_TEST_METHOD)
public class PaymentControllerTest {
    private static final String PAYMENTS_ENDPOINT = "/payments";
    private static final String PAYMENTS_ID_ENDPOINT = "/payments/{id}";
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("""
            Create payment with active rental ID - Not Found
            """)
    @WithUserDetails("testmail2@mail.com")
    void createPaymentWithActiveRental_asCustomer_notFound() throws Exception {
        PaymentRequestDto dto = new PaymentRequestDto(1L);

        String jsonRequest = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                post(PAYMENTS_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    @DisplayName("""
            Find payment with existing ID - Success
            """)
    @WithUserDetails("testmail2@mail.com")
    void findPaymentWithExistingId_asCustomer_ok() throws Exception {
        MvcResult result = mockMvc.perform(
                get(PAYMENTS_ID_ENDPOINT, 1L)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        PaymentResponseDto expected = TestObjectBuilder.initExpectedPaymentResponseDto();
        PaymentResponseDto actual = objectMapper.readValue(jsonResponse, PaymentResponseDto.class);

        assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(BigDecimal.class)
                .isEqualTo(actual);
    }

    @Test
    @DisplayName("""
            Find payment with non-existing ID - Not Found
            """)
    @WithUserDetails("testmail@mail.com")
    void findPaymentWithNoExistingId_asCustomer_notFound() throws Exception {
        mockMvc.perform(
                get(PAYMENTS_ID_ENDPOINT, 55L)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    @DisplayName("""
            Display all payments as user - Success
            """)
    @WithUserDetails("testmail2@mail.com")
    void displayAllPaymentsAsUser_asCustomer_ok() throws Exception {
        MvcResult result = mockMvc.perform(
                get(PAYMENTS_ENDPOINT)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();
        String jsonResponse = result.getResponse().getContentAsString();

        List<PaymentResponseDto> expected = List.of(
                TestObjectBuilder.initExpectedPaymentResponseDto());
        List<PaymentResponseDto> actual = objectMapper
                .readValue(jsonResponse, new TypeReference<>() {});

        assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(BigDecimal.class)
                .isEqualTo(actual);
    }
}
