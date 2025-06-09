package model.dto;

import lombok.Builder;

@Builder
public record UserRespondDto(
        String uuid,
        String username,
        String email
) {
}
