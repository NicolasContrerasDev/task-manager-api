package com.portfolio.tareas.tareas_api.exceptions;

import com.portfolio.tareas.tareas_api.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(
		MethodArgumentNotValidException ex,
		HttpServletRequest request
	) {
		Map<String, String> validationErrors = new LinkedHashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			validationErrors.putIfAbsent(error.getField(), error.getDefaultMessage());
		}

		ApiError apiError = new ApiError(
			Instant.now(),
			HttpStatus.BAD_REQUEST.value(),
			HttpStatus.BAD_REQUEST.getReasonPhrase(),
			"Hay errores de validacion en la solicitud",
			request.getRequestURI(),
			validationErrors
		);

		return ResponseEntity.badRequest().body(apiError);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiError> handleResponseStatus(
		ResponseStatusException ex,
		HttpServletRequest request
	) {
		HttpStatusCode statusCode = ex.getStatusCode();
		HttpStatus status = HttpStatus.resolve(statusCode.value());
		String error = status != null ? status.getReasonPhrase() : "Error";
		String message = ex.getReason() != null ? ex.getReason() : error;

		ApiError apiError = new ApiError(
			Instant.now(),
			statusCode.value(),
			error,
			message,
			request.getRequestURI(),
			Map.of()
		);

		return ResponseEntity.status(statusCode).body(apiError);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiError> handleAuthentication(
		AuthenticationException ex,
		HttpServletRequest request
	) {
		ApiError apiError = new ApiError(
			Instant.now(),
			HttpStatus.UNAUTHORIZED.value(),
			HttpStatus.UNAUTHORIZED.getReasonPhrase(),
			"Credenciales invalidas",
			request.getRequestURI(),
			Map.of()
		);

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiError> handleDataIntegrity(
		DataIntegrityViolationException ex,
		HttpServletRequest request
	) {
		ApiError apiError = new ApiError(
			Instant.now(),
			HttpStatus.CONFLICT.value(),
			HttpStatus.CONFLICT.getReasonPhrase(),
			"Ya existe un registro con esos datos",
			request.getRequestURI(),
			Map.of()
		);

		return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
	}
}
