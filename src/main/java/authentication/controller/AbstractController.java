package authentication.controller;

import org.apache.naming.java.javaURLContextFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import authentication.dto.ValidatedResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractController {

	private static final String FIELD_ERRORS_NAME = "fieldErrors";
	private static final String GLOBAL_ERRORS_NAME = "globalErrors";

	public ValidatedResponse createErrorValidationResponse(BindingResult result) {
		List<FieldError> fieldErrors = result.getFieldErrors();
		List<ObjectError> globalErrors = result.getGlobalErrors();

		Map<String, Object> errors = new HashMap<>();
		errors.put(FIELD_ERRORS_NAME, parseFieldErrors(fieldErrors));
		errors.put(GLOBAL_ERRORS_NAME, parseObjectErrors(globalErrors));

		ValidatedResponse response = new ValidatedResponse();
		response.setValid(false);
		response.setBody(errors);

		return response;
	}

	public ValidatedResponse createSuccessValidationResponse(Object body) {
		ValidatedResponse response = new ValidatedResponse();

		response.setValid(true);
		response.setBody(body);

		return response;
	}

	private Map<String, String> parseFieldErrors(List<FieldError> errors) {
		Map<String, String> parsedErrors = new HashMap<>();

		errors.forEach(error -> {
			parsedErrors.put(error.getField(), error.getDefaultMessage());
		});

		return parsedErrors;
	}

	private List<String> parseObjectErrors(List<ObjectError> errors) {
		List<String> parsedErrors = new ArrayList<>();

		errors.forEach(error -> {
			parsedErrors.add(error.getDefaultMessage());
		});

		return parsedErrors;
	}

}
