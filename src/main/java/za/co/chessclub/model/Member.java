package za.co.chessclub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
    private String email;
    private LocalDate birthday;
    @Column(name = "game_played")
    @Min(value = 0, message = "Number of games played must be zero or more")
    private int gamesPlayed;
    private int rank;

    @CreationTimestamp
    @Column(name = "create_timestamp")
    private LocalDateTime creationTs;

    @UpdateTimestamp
    @Column(name = "updated_timestamp")
    private LocalDateTime updateTs;

}
