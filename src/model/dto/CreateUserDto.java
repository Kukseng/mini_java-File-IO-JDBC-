package model.dto;

import lombok.Builder;

@Builder
public record CreateUserDto(
        String username,
        String email,
        String password
) {
}
