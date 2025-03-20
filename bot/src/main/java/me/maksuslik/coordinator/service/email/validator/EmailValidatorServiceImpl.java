package me.maksuslik.coordinator.service.email.validator;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Data
@Service
public class EmailValidatorServiceImpl implements EmailValidatorService {

    Pattern emailPattern;

    @Override
    public boolean isValid(String email) {
        return this.emailPattern.matcher(email).matches();
    }
}
