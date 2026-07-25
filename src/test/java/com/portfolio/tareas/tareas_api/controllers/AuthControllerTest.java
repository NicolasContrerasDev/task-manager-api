package com.portfolio.tareas.tareas_api.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.portfolio.tareas.tareas_api.dto.AuthResponse;
import com.portfolio.tareas.tareas_api.dto.RegisterRequest;
import com.portfolio.tareas.tareas_api.dto.UserResponse;
import com.portfolio.tareas.tareas_api.config.JwtAuthenticationFilter;
import com.portfolio.tareas.tareas_api.exceptions.ApiExceptionHandler;
import com.portfolio.tareas.tareas_api.services.AuthService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.FilterType;

@WebMvcTest(
	controllers = AuthController.class,
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@Test
	void registerRejectsPasswordWithEightCharacters() throws Exception {
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"username": "nico",
						"email": "nico@example.com",
						"password": "12345678"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.validationErrors.password").value("La contrasena debe tener mas de 8 caracteres"));

		verifyNoInteractions(authService);
	}

	@Test
	void registerReturnsJwtForValidRequest() throws Exception {
		UserResponse user = new UserResponse(
			UUID.randomUUID(),
			"nico",
			"nico@example.com",
			"USER",
			LocalDateTime.parse("2026-01-01T00:00:00")
		);
		when(authService.register(any(RegisterRequest.class)))
			.thenReturn(AuthResponse.bearer("jwt-token", 86400000L, user));

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"username": "nico",
						"email": "nico@example.com",
						"password": "123456789"
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.token").value("jwt-token"))
			.andExpect(jsonPath("$.tokenType").value("Bearer"))
			.andExpect(jsonPath("$.user.username").value("nico"));
	}
}
