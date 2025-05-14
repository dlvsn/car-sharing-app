package denys.mazurenko.easydrive.contoller;

import static denys.mazurenko.easydrive.util.DatabaseTestScripts.DELETE_ROLES_USERS_SQL;
import static denys.mazurenko.easydrive.util.DatabaseTestScripts.DELETE_USERS_SQL;
import static denys.mazurenko.easydrive.util.DatabaseTestScripts.INSERT_USERS_SQL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import denys.mazurenko.easydrive.dto.user.UpdateProfileInfoRequestDto;
import denys.mazurenko.easydrive.dto.user.UpdateRolesRequestDto;
import denys.mazurenko.easydrive.dto.user.UserResponseDto;
import denys.mazurenko.easydrive.util.TestObjectBuilder;
import java.util.Collections;
import java.util.Set;
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
        INSERT_USERS_SQL
}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = {
        DELETE_ROLES_USERS_SQL,
        DELETE_USERS_SQL
}, executionPhase = AFTER_TEST_METHOD)
public class UserControllerTest {
    private static final Long TEST_USER_ID = 2L;
    private static final String USERS_ID_ENDPOINT = "/users/{id}/role";
    private static final String USERS_ME_ENDPOINT = "/users/me";
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
            Get profile information of authorized user - Success
            """)
    @WithUserDetails("testmail@mail.com")
    void getProfileInfo_asCutomer_success() throws Exception {
        MvcResult result = mockMvc.perform(
                get(USERS_ME_ENDPOINT)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();

        UserResponseDto expected = TestObjectBuilder.initUserResponseDto();
        UserResponseDto actual = objectMapper.readValue(jsonResponse, UserResponseDto.class);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    @DisplayName("""
            Update profile information of authorized user - Success
            """)
    @WithUserDetails("testmail@mail.com")
    void updateProfileInfoOfAuthorized_asCustomer_success() throws Exception {
        UpdateProfileInfoRequestDto dto = TestObjectBuilder.initValidUpdateProfileDto();
        String jsonRequest = objectMapper.writeValueAsString(dto);

        MvcResult result = mockMvc.perform(
                        put(USERS_ME_ENDPOINT)
                                .content(jsonRequest)
                                .contentType(APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();

        UserResponseDto expected = new UserResponseDto();
        expected.setEmail("testmail@mail.com");
        expected.setFirstName(dto.getFirstName());
        expected.setLastName(dto.getLastName());

        String jsonResponse = result.getResponse().getContentAsString();
        UserResponseDto actual = objectMapper.readValue(jsonResponse, UserResponseDto.class);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    @DisplayName("""
            Update profile information of authorized user with invalid DTO - Bad Request
            """)
    @WithUserDetails("testmail@mail.com")
    void updateProfileInfoOfAuthorizedUserInvalidDto_asCutomer_badRequest() throws Exception {
        UpdateProfileInfoRequestDto dto = TestObjectBuilder.initInvalidUpdateProfileDto();
        String jsonRequest = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                put(USERS_ME_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @DisplayName("""
            Update user role with valid DTO - Success
            """)
    @WithMockUser(username = "admin", roles = "MANAGER")
    void updateUserRoleWithValidDto_asManager_success() throws Exception {
        UpdateRolesRequestDto requestDto = new UpdateRolesRequestDto(Set.of(1L, 2L));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                put(USERS_ID_ENDPOINT, TEST_USER_ID)
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        UserResponseDto userResponseDto = objectMapper
                .readValue(jsonResponse, UserResponseDto.class);

        assertThat(userResponseDto).isNotNull();
    }

    @Test
    @DisplayName("""
            Update role with empty set of roles - Bad Request
            """)
    @WithMockUser(username = "admin", roles = "MANAGER")
    void updateRoleWithEmptySetOfRoles_asManager_badRequest() throws Exception {
        UpdateRolesRequestDto requestDto = new UpdateRolesRequestDto(Collections.emptySet());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                put(USERS_ID_ENDPOINT, TEST_USER_ID)
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @DisplayName("""
            Update role with no existing IDs of roles - Not Found
            """)
    @WithMockUser(username = "admin", roles = "MANAGER")
    void updateRoleWithNoExistingIdsOfRoles_asManager_NotFound() throws Exception {
        UpdateRolesRequestDto requestDto = new UpdateRolesRequestDto(Set.of(55L, 34L));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                put(USERS_ID_ENDPOINT, TEST_USER_ID)
                        .content(jsonRequest)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    @DisplayName("""
            Update role with no existing user ID - Not Found
            """)
    @WithMockUser(username = "admin", roles = "MANAGER")
    void updateRoleWithNoExistingUserId_asManager_notFound() throws Exception {
        UpdateRolesRequestDto requestDto = new UpdateRolesRequestDto(Set.of(1L));
        String jsonResponse = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                put(USERS_ID_ENDPOINT, 55L)
                        .content(jsonResponse)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isNotFound()).andReturn();
    }
}
