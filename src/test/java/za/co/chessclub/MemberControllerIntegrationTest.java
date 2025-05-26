package za.co.chessclub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import za.co.chessclub.model.*;
import za.co.chessclub.repository.MemberRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository memberRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/members";
    }

    @BeforeEach
    void setup() {
        memberRepository.deleteAll();
        memberRepository.flush();
    }

    @Test
    void testCreateAndGetMember() {
        var memberCreateDTO = new MemberCreateDTO("Nicolas", "Rasekgala",
                "nicolas@gmail.com", LocalDate.of(1995, 3, 15), 0);

        ResponseEntity<MemberDTO> response = restTemplate.postForEntity(getBaseUrl(), memberCreateDTO, MemberDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Nicolas");

        // Get all members and verify
        ResponseEntity<MemberDTO[]> getAllResponse = restTemplate.getForEntity(getBaseUrl(), MemberDTO[].class);
        assertThat(getAllResponse.getBody()).hasSize(1);
    }

    @Test
    void testUpdateMember() {
        var memberCreateDTO = new MemberCreateDTO("Tebogo", "Molele",
                "tebogo@gmail.com", LocalDate.of(1990, 3, 15), 0);

        MemberDTO saved = restTemplate.postForEntity(getBaseUrl(), memberCreateDTO, MemberDTO.class).getBody();
        assertNotNull(saved);
        assertThat(saved.name()).isEqualTo("Tebogo");

        var updateDTO = new MemberUpdateDTO("John",  saved.surname(), saved.email(), saved.birthday());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MemberUpdateDTO> entity = new HttpEntity<>(updateDTO, headers);

        ResponseEntity<MemberDTO> updatedResponse = restTemplate.exchange(
                getBaseUrl() + "/" + saved.id(),
                HttpMethod.PUT,
                entity,
                MemberDTO.class
        );

        assertThat(updatedResponse.getBody()).isNotNull();
        assertThat(updatedResponse.getBody().name()).isEqualTo("John");
    }

    @Test
    void testDeleteMember() {
        var memberCreateDTO = new MemberCreateDTO("Jack", "Smith",
                "jack.smith@gmail.com", LocalDate.of(1992, 4, 20), 0);

        MemberDTO saved = restTemplate.postForEntity(getBaseUrl(), memberCreateDTO, MemberDTO.class).getBody();
        assertNotNull(saved);

        restTemplate.delete(getBaseUrl() + "/" + saved.id());

        ResponseEntity<MemberDTO[]> response = restTemplate.getForEntity(getBaseUrl(), MemberDTO[].class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void testMatchDraw_RankUpdate() {
        MemberDTO player1 = createMember("Alice", 10);
        MemberDTO player2 = createMember("Bob", 15);
        MatchRequest matchResult = new MatchRequest(player1.id(), player2.id(), MatchResult.DRAW);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MatchRequest> entity = new HttpEntity<>(matchResult, headers);

        restTemplate.postForEntity(getBaseUrl() + "/match", entity, Void.class);

        // Fetch updated members
        MemberDTO updatedP1 = restTemplate.getForObject(getBaseUrl() + "/" + player1.id(), MemberDTO.class);
        MemberDTO updatedP2 = restTemplate.getForObject(getBaseUrl() + "/" + player2.id(), MemberDTO.class);

        assertThat(updatedP1.rank()).isEqualTo(10);  // unchanged
        assertThat(updatedP2.rank()).isEqualTo(14);  // moved up
    }

    @Test
    void testMatchLowerBeatsHigher_RankUpdate() {
        MemberDTO player1 = createMember("Higher", 10);
        MemberDTO player2 = createMember("Lower", 16);

        MatchRequest matchResult = new MatchRequest(player1.id(), player2.id(), MatchResult.PLAYER2_WIN);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MatchRequest> entity = new HttpEntity<>(matchResult, headers);

        restTemplate.postForEntity(getBaseUrl() + "/match", entity, Void.class);

        MemberDTO updatedP1 = restTemplate.getForObject(getBaseUrl() + "/" + player1.id(), MemberDTO.class);
        MemberDTO updatedP2 = restTemplate.getForObject(getBaseUrl() + "/" + player2.id(), MemberDTO.class);

        assertThat(updatedP2.rank()).isEqualTo(13);  // moved up by half of 16 - 10 = 6 -> 3 ranks up
        assertThat(updatedP1.rank()).isEqualTo(11);  // moved down by 1
    }

    private MemberDTO createMember(String name, int rank) {
        var memberCreateDTO = new MemberCreateDTO(name, name,
                name + "@gmail.com", LocalDate.of(1992, 4, 25), rank);

        return restTemplate.postForEntity(getBaseUrl() , memberCreateDTO, MemberDTO.class).getBody();
    }
}
