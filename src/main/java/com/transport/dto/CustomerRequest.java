package com.transport.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
		@NotBlank String name,
        String phone,
        String address,
        String company)
		 {

	
}
