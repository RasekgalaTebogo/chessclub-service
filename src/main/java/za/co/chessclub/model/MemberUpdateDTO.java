package za.co.chessclub.model;

import java.time.LocalDate;

public record MemberUpdateDTO (String name, String surname,
                               String email, LocalDate birthday ) { }
