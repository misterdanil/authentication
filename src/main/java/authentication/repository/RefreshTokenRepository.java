package authentication.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import authentication.model.RefreshToken;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
	RefreshToken findByToken(String token);
	
	void removeByToken(String token);
}
