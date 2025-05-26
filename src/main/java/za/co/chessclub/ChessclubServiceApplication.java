package za.co.chessclub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ChessclubServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChessclubServiceApplication.class, args);
	}

}
