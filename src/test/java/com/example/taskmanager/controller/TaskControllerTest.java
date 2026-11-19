package com.example.taskmanager.controller;

import com.example.taskmanager.config.SecurityConfig;
import com.example.taskmanager.security.CustomAccessDeniedHandler;
import com.example.taskmanager.security.CustomAuthEntryPoint;
import com.example.taskmanager.security.JwtFilter;
import com.example.taskmanager.security.JwtService;
import com.example.taskmanager.service.TaskService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import({
        SecurityConfig.class,
        JwtFilter.class,
        CustomAuthEntryPoint.class,
        CustomAccessDeniedHandler.class
})
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void getAll_whenNotAuthenticated_shouldReturn401()
            throws Exception {

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAll_whenAuthenticated_shouldReturn200()
            throws Exception {

        when(taskService.getAll(isNull(), isNull()))
                .thenReturn(List.of());

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void delete_whenRoleUser_shouldReturn403()
            throws Exception {

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_whenRoleAdmin_shouldReturn204()
            throws Exception {

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }
}