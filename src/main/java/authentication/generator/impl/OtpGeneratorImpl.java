package authentication.generator.impl;

import java.util.Random;

import org.springframework.stereotype.Component;

import authentication.generator.OtpGenerator;

@Component
public class OtpGeneratorImpl implements OtpGenerator<Integer> {

	@Override
	public String generate(Integer source) {
		Random random = new Random();

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < source; i++) {
			builder.append(random.nextInt(10));
		}

		return builder.toString();
	}

}
