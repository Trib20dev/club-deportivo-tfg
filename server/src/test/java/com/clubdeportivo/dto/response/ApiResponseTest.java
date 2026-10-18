package com.clubdeportivo.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ApiResponse Tests")
class ApiResponseTest {

    @Test
    @DisplayName("Should create successful response with data")
    void testSuccessWithMessageAndData() {
        String message = "Operación exitosa";
        String data = "Datos de prueba";

        ApiResponse<String> response = ApiResponse.success(message, data);

        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertNotNull(response.getTimestamp());
        assertTrue(response.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Should create successful response without data")
    void testSuccessWithMessageOnly() {
        String message = "Operación completada";

        ApiResponse<Void> response = ApiResponse.success(message);

        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Should create error response")
    void testErrorWithMessage() {
        String errorMessage = "Ocurrió un error";

        ApiResponse<Object> response = ApiResponse.error(errorMessage);

        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Should construct with no-arg and default timestamp")
    void testNoArgsConstructor() {
        ApiResponse<String> response = new ApiResponse<>();

        assertNotNull(response.getTimestamp());
        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Should use builder with default timestamp")
    void testBuilder() {
        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
                .success(true)
                .message("OK")
                .data(123)
                .build();

        assertTrue(response.isSuccess());
        assertEquals("OK", response.getMessage());
        assertEquals(123, response.getData());
        assertNotNull(response.getTimestamp());
    }
}

