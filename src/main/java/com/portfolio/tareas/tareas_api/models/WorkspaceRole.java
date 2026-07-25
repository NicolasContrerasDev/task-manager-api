package com.portfolio.tareas.tareas_api.models;

public enum WorkspaceRole {
	OWNER,
	ADMIN,
	USER;

	public boolean canManageMembers() {
		return this == OWNER || this == ADMIN;
	}

	public boolean canManageTasks() {
		return this == OWNER || this == ADMIN;
	}
}
