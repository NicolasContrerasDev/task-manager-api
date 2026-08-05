package com.portfolio.tareas.tareas_api.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.portfolio.tareas.tareas_api.config.JwtAuthenticationFilter;
import com.portfolio.tareas.tareas_api.dto.WorkspaceJoinRequestResponse;
import com.portfolio.tareas.tareas_api.services.WorkspaceService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
	controllers = WorkspaceController.class,
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class WorkspaceControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private WorkspaceService workspaceService;

	@Test
	void joinWorkspaceAcceptsUuidPathVariable() throws Exception {
		UUID workspaceId = UUID.randomUUID();
		WorkspaceJoinRequestResponse response = new WorkspaceJoinRequestResponse(
			UUID.randomUUID(),
			workspaceId,
			UUID.randomUUID(),
			"nico",
			"nico@example.com",
			"PENDING",
			LocalDateTime.parse("2026-01-01T00:00:00")
		);
		when(workspaceService.joinWorkspace(workspaceId)).thenReturn(response);

		mockMvc.perform(post("/api/workspaces/{workspaceId}/join", workspaceId))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("PENDING"));

		verify(workspaceService).joinWorkspace(workspaceId);
	}
}