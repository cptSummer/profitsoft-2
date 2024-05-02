package org.profitsoft.profitsoft2;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.profitsoft.profitsoft2.database.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @Test
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    public void testCreateUserOk() throws Exception {
        doNothing().when(userService).createUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .param("username", "testUser")
                        .param("email", "testEmail@test.com")
                        .param("password", "testPassword"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreateUserFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .param("username", "testUser")
                        .param("email", "testEmail"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUpdateUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/1")
                        .param("username", "testUser")
                        .param("email", "testEmail")
                        .param("password", "testPassword"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
