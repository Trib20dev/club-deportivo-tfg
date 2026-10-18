package com.clubdeportivo.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException and return 404 NOT_FOUND")
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Usuario no encontrado");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Usuario no encontrado", response.getBody().getMessage());
        assertFalse(response.getBody().getDetails().isEmpty());
        assertEquals("Usuario no encontrado", response.getBody().getDetails().get(0));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Should handle BadRequestException and return 400 BAD_REQUEST")
    void testHandleBadRequestException() {
        BadRequestException ex = new BadRequestException("Parámetro inválido");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadRequestException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Parámetro inválido", response.getBody().getMessage());
        assertFalse(response.getBody().getDetails().isEmpty());
    }

    @Test
    @DisplayName("Should handle DuplicateResourceException and return 400 BAD_REQUEST")
    void testHandleDuplicateResourceException() {
        DuplicateResourceException ex = new DuplicateResourceException("El email ya está registrado");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateResourceException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("El email ya está registrado", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle UnauthorizedException and return 401 UNAUTHORIZED")
    void testHandleUnauthorizedException() {
        UnauthorizedException ex = new UnauthorizedException("Credenciales inválidas");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnauthorizedException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Unauthorized", response.getBody().getError());
        assertEquals("Credenciales inválidas", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException and extract field errors")
    void testHandleMethodArgumentNotValidException() throws NoSuchMethodException {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "email", "El correo es obligatorio"));
        bindingResult.addError(new FieldError("target", "password", "La contraseña debe tener al menos 8 caracteres"));

        Method method = this.getClass().getDeclaredMethod("dummyMethod", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValidException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Error de validación en los campos de la solicitud", response.getBody().getMessage());
        assertEquals(2, response.getBody().getDetails().size());
        assertTrue(response.getBody().getDetails().contains("email: El correo es obligatorio"));
        assertTrue(response.getBody().getDetails().contains("password: La contraseña debe tener al menos 8 caracteres"));
    }

    @Test
    @DisplayName("Should handle generic Exception and return 500 INTERNAL_SERVER_ERROR")
    void testHandleGenericException() {
        Exception ex = new RuntimeException("NullPointerException inesperado");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Ha ocurrido un error interno en el servidor. Por favor, inténtelo de nuevo más tarde.", response.getBody().getMessage());
        assertFalse(response.getBody().getDetails().isEmpty());
    }

    // Helper dummy method for creating MethodParameter
    private void dummyMethod(String param) {
    }
}

