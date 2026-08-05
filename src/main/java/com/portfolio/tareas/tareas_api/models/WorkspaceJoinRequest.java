package com.portfolio.tareas.tareas_api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workspace_join_requests")
public class WorkspaceJoinRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "workspace_id", nullable = false)
	private Workspace workspace;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private AppUser user;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private JoinRequestStatus status = JoinRequestStatus.PENDING;

	@Column(name = "requested_at", nullable = false, updatable = false)
	private LocalDateTime requestedAt;

	@Column(name = "resolved_at")
	private LocalDateTime resolvedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "resolved_by_user_id")
	private AppUser resolvedBy;

	public WorkspaceJoinRequest() {
	}

	public WorkspaceJoinRequest(Workspace workspace, AppUser user) {
		this.workspace = workspace;
		this.user = user;
		this.status = JoinRequestStatus.PENDING;
	}

	@PrePersist
	protected void onCreate() {
		this.requestedAt = LocalDateTime.now();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}

	public AppUser getUser() {
		return user;
	}

	public void setUser(AppUser user) {
		this.user = user;
	}

	public JoinRequestStatus getStatus() {
		return status;
	}

	public void setStatus(JoinRequestStatus status) {
		this.status = status;
	}

	public LocalDateTime getRequestedAt() {
		return requestedAt;
	}

	public void setRequestedAt(LocalDateTime requestedAt) {
		this.requestedAt = requestedAt;
	}

	public LocalDateTime getResolvedAt() {
		return resolvedAt;
	}

	public void setResolvedAt(LocalDateTime resolvedAt) {
		this.resolvedAt = resolvedAt;
	}

	public AppUser getResolvedBy() {
		return resolvedBy;
	}

	public void setResolvedBy(AppUser resolvedBy) {
		this.resolvedBy = resolvedBy;
	}
}