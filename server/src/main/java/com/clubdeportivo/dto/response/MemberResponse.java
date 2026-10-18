package com.clubdeportivo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class MemberResponse {

    private UUID id;
    private String email;
    private String dni;
    private String name;
    private String lastName;
    private String phone;
    private String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate joinDate;

    private String status;
    private String membershipType;
    private BigDecimal discountPercentage;

    public MemberResponse() {
    }

    public MemberResponse(UUID id, String email, String dni, String name, String lastName,
                          String phone, String address, LocalDate joinDate, String status,
                          String membershipType, BigDecimal discountPercentage) {
        this.id = id;
        this.email = email;
        this.dni = dni;
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.joinDate = joinDate;
        this.status = status;
        this.membershipType = membershipType;
        this.discountPercentage = discountPercentage;
    }
}

