package authentication.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bebracore.cabinet.model.VerificationToken;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {

	VerificationToken findByToken(String token);

}
