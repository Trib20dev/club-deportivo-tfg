package com.clubdeportivo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateMemberStatusRequest {

    @NotBlank(message = "El nuevo estado no puede estar vacío")
    private String status;

    public UpdateMemberStatusRequest() {
    }

    public UpdateMemberStatusRequest(String status) {
        this.status = status;
    }
}

