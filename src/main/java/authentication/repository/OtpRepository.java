package authentication.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import authentication.model.Otp;

@Repository
public interface OtpRepository extends MongoRepository<Otp, String> {
	Otp findByCode(String code);

	boolean existsByCode(String code);
}
