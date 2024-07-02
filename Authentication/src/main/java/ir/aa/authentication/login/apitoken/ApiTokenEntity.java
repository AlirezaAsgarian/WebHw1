package ir.aa.authentication.login.apitoken;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ApiTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String username;
    private String expireDate; // Use String or OffsetDateTime if needed
    private String token;
    private boolean isActive;

    public ApiTokenEntity(String name, String username, String expireDate, String token) {
        this.name = name;
        this.username = username;
        this.expireDate = expireDate;
        this.token = token;
        this.isActive = true;
    }

}

