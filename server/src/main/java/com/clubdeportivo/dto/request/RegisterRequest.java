package com.clubdeportivo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterRequest {

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String lastName;

    @NotBlank(message = "El DNI no puede estar vacío")
    private String dni;

    private String phone;

    private String address;

    public RegisterRequest() {
    }

    public RegisterRequest(String email, String password, String name, String lastName, String dni, String phone, String address) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
        this.dni = dni;
        this.phone = phone;
        this.address = address;
    }
}
