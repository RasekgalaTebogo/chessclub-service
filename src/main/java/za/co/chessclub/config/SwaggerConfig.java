package za.co.chessclub.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "Chess Club API", version = "1.0", description = "Manage members and rankings for the chess club")
)
@Configuration
public class SwaggerConfig {
}
