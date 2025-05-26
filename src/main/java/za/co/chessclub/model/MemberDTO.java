package za.co.chessclub.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemberDTO( Long id, String name, String surname,
                        String email, LocalDate birthday, int gamesPlayed, int rank,
                        LocalDateTime creationTs, LocalDateTime updateTs ) { }
