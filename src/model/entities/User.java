package model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    private String uuid;
    private String username;
    private String email;
    private String password;
    private Boolean isDeleted;
    private LocalDateTime createdAt;

    public User(String username, String email, String password) {
        this.uuid = UUID.randomUUID().toString();
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now();
        this.username = username;
        this.email = email;
        this.password = password;
    }
}

