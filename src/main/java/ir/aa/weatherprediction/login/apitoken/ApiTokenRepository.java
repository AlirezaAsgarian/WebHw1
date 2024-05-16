package ir.aa.weatherprediction.login.apitoken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiTokenRepository extends JpaRepository<ApiTokenEntity, Integer> {
    // Define custom query methods here if needed
    Optional<ApiTokenEntity> findByToken(String token);

    Optional<ApiTokenEntity> findByName(String name);
    List<ApiTokenEntity> findByUsername(String username);

}

