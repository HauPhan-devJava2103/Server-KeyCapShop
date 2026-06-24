package com.vn.keycap_server.cases.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.vn.keycap_server.base.BaseApiTest;
import com.vn.keycap_server.dto.request.user.UpdateProfileRequest;
import com.vn.keycap_server.helpers.AuthTestHelper;

public class UserApiTest extends BaseApiTest {

    @Autowired
    private AuthTestHelper authTestHelper;

    @Test
    @DisplayName("Get Profile Success - 200 OK")
    public void testGetProfileSuccess() throws Exception {
        String token = authTestHelper.getUserToken();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/profile")
                .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").exists());
    }

    @Test
    @DisplayName("Get Profile Unauthorized - No Token - 401 Unauthorized")
    public void testGetProfileUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/profile"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("Update Profile Success - 200 OK")
    public void testUpdateProfileSuccess() throws Exception {
        String token = authTestHelper.getUserToken();

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("Updated Name");
        request.setPhone("0987654321");

        mockMvc.perform(MockMvcRequestBuilders.patch("/user/profile")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.fullName").value("Updated Name"));
    }

    @Test
    @DisplayName("Update Profile Validation - Blank Name - 400 Bad Request")
    public void testUpdateProfileBlankName() throws Exception {
        String token = authTestHelper.getUserToken();

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("");

        mockMvc.perform(MockMvcRequestBuilders.patch("/user/profile")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
