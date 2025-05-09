package denys.mazurenko.carsharingapp.contoller;

import static denys.mazurenko.carsharingapp.util.DatabaseTestScripts.DELETE_CARS_SQL;
import static denys.mazurenko.carsharingapp.util.DatabaseTestScripts.DELETE_ROLES_USERS_SQL;
import static denys.mazurenko.carsharingapp.util.DatabaseTestScripts.DELETE_USERS_SQL;
import static denys.mazurenko.carsharingapp.util.DatabaseTestScripts.INSERT_CARS_SQL;
import static denys.mazurenko.carsharingapp.util.DatabaseTestScripts.INSERT_USERS_SQL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import denys.mazurenko.carsharingapp.dto.car.CarDto;
import denys.mazurenko.carsharingapp.dto.car.UpdateCarRequestDto;
import denys.mazurenko.carsharingapp.util.TestObjectBuilder;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
        INSERT_USERS_SQL,
        INSERT_CARS_SQL
}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = {
        DELETE_CARS_SQL,
        DELETE_ROLES_USERS_SQL,
        DELETE_USERS_SQL
}, executionPhase = AFTER_TEST_METHOD)
public class CarControllerTest {
    private static final String CARS_ENDPOINT = "/cars";
    private static final String CARS_ID_ENDPOINT = "/cars/{id}";
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
            Create car when no existing car - Success
            """)
    @WithMockUser(username = "admin", roles = "MANAGER")
    void createNoExistingCar_asManager_success() throws Exception {
        CarDto expected = TestObjectBuilder.initCarDto();
        String stringRequest = objectMapper.writeValueAsString(expected);
        MvcResult jsonRequest = mockMvc.perform(
                post(CARS_ENDPOINT)
                        .content(stringRequest)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        String jsonResponse = jsonRequest.getResponse().getContentAsString();
        CarDto actual = objectMapper.readValue(jsonResponse, CarDto.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(BigDecimal.class)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Create car with existing car - Conflict
            """)
    @WithMockUser(username = "admin", roles = "MANAGER")
    void createExistingCar_asManager_conflict() throws Exception {
        CarDto carDto = TestObjectBuilder.initFirstExistingCarDto();
        String jsonRequest = objectMapper.writeValueAsString(carDto);
        mockMvc.perform(
                post(CARS_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isConflict()).andReturn();
    }

    @Test
    @DisplayName("""
            Create car with empty DTO - Bad Request
            """)
    @WithMockUser(username = "admin", roles = "MANAGER")
    void createCarWithEmptyDto_asManager_badRequest() throws Exception {
        CarDto carDto = TestObjectBuilder.initInvalidCarRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(carDto);
        mockMvc.perform(
                post(CARS_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @DisplayName("""
            Get all cars as a customer - Success
            """)
    @WithUserDetails("testmail@mail.com")
    void getAllCars_asCustomer_success() throws Exception {
        MvcResult result = mockMvc.perform(
                get(CARS_ENDPOINT)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        List<CarDto> actual = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
        List<CarDto> expected = List.of(
                TestObjectBuilder.initFirstExistingCarDto(),
                TestObjectBuilder.initSecondExistingCarDto());

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(BigDecimal.class)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Get car with existing ID as a customer - Success
            """)
    @WithUserDetails("testmail@mail.com")
    void getCarWithExistingId_asCustomer_success() throws Exception {
        MvcResult result = mockMvc.perform(
                get(CARS_ID_ENDPOINT, 1L)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();
        String jsonResponse = result.getResponse().getContentAsString();

        CarDto actual = objectMapper.readValue(jsonResponse, CarDto.class);
        CarDto expected = TestObjectBuilder.initFirstExistingCarDto();

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(BigDecimal.class)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Get car with non-existing ID as a customer - Not Found
            """)
    @WithUserDetails("testmail@mail.com")
    void getCarWithNoExistingId_asCustomer_notFound() throws Exception {
        mockMvc.perform(
                get(CARS_ID_ENDPOINT, 99L)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    @DisplayName("""
            Update car with existing ID and valid request as manager - Success
            """)
    @WithMockUser(username = "admin", roles = "MANAGER")
    void updateCarWithExistingIdAndValidRequest_asManager_success() throws Exception {
        UpdateCarRequestDto dto = TestObjectBuilder.initUpdateCarDto();
        String jsonRequest = objectMapper.writeValueAsString(dto);
        MvcResult result = mockMvc.perform(
                put(CARS_ID_ENDPOINT, 1L)
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        CarDto actual = objectMapper.readValue(jsonResponse, CarDto.class);
        CarDto expected = TestObjectBuilder.initUpdatedCarDto(
                TestObjectBuilder.initFirstExistingCarDto(), dto);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(BigDecimal.class)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Update car with non-existing ID as manager - Not Found
            """)
    @WithMockUser(username = "admin", roles = "MANAGER")
    void updateCarWithNoExistingId_asManager_notFound() throws Exception {
        mockMvc.perform(
                get(CARS_ID_ENDPOINT, 19L)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    @DisplayName("""
            Delete car with existing ID as manager - No Content
            """)
    @WithMockUser(username = "admin", roles = "MANAGER")
    void deleteCarWithExistingId_asManager_noContent() throws Exception {
        mockMvc.perform(
                delete(CARS_ID_ENDPOINT, 1L)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isNoContent()).andReturn();
    }
}
