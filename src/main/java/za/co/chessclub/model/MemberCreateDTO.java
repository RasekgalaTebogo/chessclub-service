package za.co.chessclub.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record MemberCreateDTO(@NotEmpty String name, @NotEmpty String surname,
                              @NotEmpty @Email String email, @Past LocalDate birthday, int rank ) { }
