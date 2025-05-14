package denys.mazurenko.easydrive.contoller;

import static denys.mazurenko.easydrive.util.DatabaseTestScripts.DELETE_CARS_SQL;
import static denys.mazurenko.easydrive.util.DatabaseTestScripts.DELETE_RENTALS_SQL;
import static denys.mazurenko.easydrive.util.DatabaseTestScripts.DELETE_ROLES_USERS_SQL;
import static denys.mazurenko.easydrive.util.DatabaseTestScripts.DELETE_USERS_SQL;
import static denys.mazurenko.easydrive.util.DatabaseTestScripts.INSERT_CARS_SQL;
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
import denys.mazurenko.easydrive.dto.rental.RentalRequestDto;
import denys.mazurenko.easydrive.dto.rental.RentalResponseDto;
import denys.mazurenko.easydrive.util.TestObjectBuilder;
import java.time.LocalDateTime;
import java.util.Collections;
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
        INSERT_RENTALS_SQL
}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = {
        DELETE_RENTALS_SQL,
        DELETE_CARS_SQL,
        DELETE_ROLES_USERS_SQL,
        DELETE_USERS_SQL
}, executionPhase = AFTER_TEST_METHOD)
public class RentalControllerTest {
    private static final String RENTALS_ENDPOINT = "/rentals";
    private static final String RENTALS_RETURN_ENDPOINT = "/rentals/return";
    private static final String RENTALS_ID_ENDPOINT = "/rentals/{id}";
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
            Rent car with no active rental and positive inventory - Success
            """)
    @WithUserDetails("testmail@mail.com")
    void rentCarWithNoActiveRentalAndPositiveInventory_asCustomer_success()
            throws Exception {
        RentalRequestDto rentalRequestDto = new RentalRequestDto(1L, 2);
        String jsonRequest = objectMapper.writeValueAsString(rentalRequestDto);

        MvcResult result = mockMvc.perform(
                post(RENTALS_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        RentalResponseDto actual = objectMapper.readValue(jsonResponse, RentalResponseDto.class);
        RentalResponseDto expected = TestObjectBuilder.initRentalResponseDto();

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Rent car with active rental - Conflict
            """)
    @WithUserDetails("testmail2@mail.com")
    void rentCarWithActiveRental_asCustomer_conflict()
            throws Exception {
        RentalRequestDto rentalRequestDto = new RentalRequestDto(1L, 2);
        String jsonRequest = objectMapper.writeValueAsString(rentalRequestDto);

        mockMvc.perform(
                post(RENTALS_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isConflict()).andReturn();
    }

    @Test
    @DisplayName("""
            Rent car with zero inventory - Not Found
            """)
    @WithUserDetails("testmail@mail.com")
    void rentCarWithZeroInventory_asCustomer_notFound()
            throws Exception {
        RentalRequestDto rentalRequestDto = new RentalRequestDto(2L, 2);
        String jsonRequest = objectMapper.writeValueAsString(rentalRequestDto);

        mockMvc.perform(
                post(RENTALS_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    @DisplayName("""
            Return car with active rental - Success
            """)
    @WithUserDetails("testmail2@mail.com")
    void returnCarWithActiveRental_asCustomer_success()
            throws Exception {
        MvcResult result = mockMvc.perform(
                post(RENTALS_RETURN_ENDPOINT)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        RentalResponseDto expected = TestObjectBuilder.initCompletedRentalResponseDto();
        RentalResponseDto actual = objectMapper.readValue(jsonResponse, RentalResponseDto.class);

        assertThat(actual.getActualReturnDate()).isNotNull();
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Return car with no active rental - Not Found
            """)
    @WithUserDetails("testmail@mail.com")
    void returnCarWithNoActiveRental_asCustomer_notFound()
            throws Exception {
        mockMvc.perform(
                post(RENTALS_RETURN_ENDPOINT)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    @DisplayName("""
            Get rental with existing rental ID - Success
            """)
    @WithUserDetails("testmail2@mail.com")
    void getRentalWithExistingRentalId_asCustomer_success()
            throws Exception {
        MvcResult result = mockMvc.perform(
                get(RENTALS_ID_ENDPOINT, 1L)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        RentalResponseDto expected = TestObjectBuilder.initCompletedRentalResponseDto();
        RentalResponseDto actual = objectMapper.readValue(jsonResponse, RentalResponseDto.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Get rental with non-existing rental ID - Not Found
            """)
    @WithUserDetails("testmail@mail.com")
    void getRentalWithNoExistingRentalId_asCustomer_notFound()
            throws Exception {
        mockMvc.perform(
                get(RENTALS_ID_ENDPOINT, 55L)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    @DisplayName("""
            Get active rental - Success
            """)
    @WithUserDetails("testmail2@mail.com")
    void getActiveRental_success()
            throws Exception {
        MvcResult result = mockMvc.perform(
                get(RENTALS_ENDPOINT)
                        .param("is_active", "true")
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();
        String jsonResponse = result.getResponse().getContentAsString();

        List<RentalResponseDto> expected = List.of(
                TestObjectBuilder.initCompletedRentalResponseDto()
        );
        List<RentalResponseDto> actual = objectMapper
                .readValue(jsonResponse, new TypeReference<>() {});

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Get no active rentals - Success
            """)
    @WithUserDetails("testmail2@mail.com")
    void getNoActiveRentals_asCustomer_success()
            throws Exception {
        MvcResult result = mockMvc.perform(
                get(RENTALS_ENDPOINT)
                        .param("is_active", "false")
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();
        String jsonResponse = result.getResponse().getContentAsString();

        List<RentalResponseDto> expected = Collections.emptyList();
        List<RentalResponseDto> actual = objectMapper
                .readValue(jsonResponse, new TypeReference<>() {});

        assertThat(actual).isEmpty();
        assertThat(actual).isEqualTo(expected);
    }
}
