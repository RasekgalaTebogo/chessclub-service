package za.co.chessclub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import za.co.chessclub.error.MemberNotFoundException;
import za.co.chessclub.mapper.MemberMapper;
import za.co.chessclub.model.*;
import za.co.chessclub.repository.MemberRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {


    private final MemberRepository repository;
    private final MemberMapper mapper;

    /**
     * Adds a new member to the club.
     * Automatically assigns them the last rank.
     */
    public MemberDTO addMember(MemberCreateDTO dto) {
        Member member = mapper.toEntity(dto);

        if (member.getRank() == 0) {
            int newRank = repository.findAll().size() + 1;
            member.setRank(newRank);
        }

        member.setGamesPlayed(0);
        return mapper.toDTO(repository.save(member));
    }

    /**
     * Retrieves all members sorted by rank (ascending).
     * This is used for displaying the leaderboard.
     */
    @Cacheable("leaderboard")
    public List<MemberDTO> getAllMembers() {
        return mapper.toDTOs(repository.findAllByOrderByRankAsc());
    }

    /**
     * Gets a single member by ID, wrapped in Optional.
     * Helps avoid nulls when checking if a member exists.
     */
    @Cacheable(value = "members", key = "#id")
    public MemberDTO getMember(Long id) {
        return repository.findById(id).map(mapper::toDTO)
                .orElseThrow(() -> new MemberNotFoundException("Member not found"));
    }

    /**
     * Updates an existing member’s profile details (except rank and games played).
     */
    public MemberDTO updateMember(Long id, MemberUpdateDTO updated) {
        return repository.findById(id).map(m -> {
            m.setName(updated.name() != null ? updated.name()  : m.getName());
            m.setSurname(updated.surname() != null ? updated.surname() : m.getSurname());
            m.setEmail(updated.email() != null ? updated.email() : m.getEmail());
            m.setBirthday(updated.birthday() != null ? updated.birthday() : m.getBirthday());
            return repository.save(m);
        }).map(mapper::toDTO).orElseThrow(() -> new MemberNotFoundException("Member not found"));
    }

    /**
     * Deletes a member and shifts up the ranks of players below them.
     */
    @CacheEvict(cacheNames = { "members", "leaderboard" }, allEntries = true)
    public void deleteMember(Long id) {
        repository.findById(id).ifPresent(deleted -> {
            int deletedRank = deleted.getRank();
            repository.deleteById(id);

            // All members with a rank below the deleted one are moved up
            repository.findAll().stream()
                    .filter(m -> m.getRank() > deletedRank)
                    .forEach(m -> {
                        m.setRank(m.getRank() - 1);
                        repository.save(m);
                    });
        });
    }

    /**
     * Processes a match between two players and adjusts their ranks based on the result.
     * Rules:
     * - Higher-ranked player wins: no rank change
     * - Draw: lower-ranked player gains 1 position if ranks are not adjacent
     * - Lower-ranked player wins: rank changes for both based on rank difference
     */
    @CacheEvict(value = "leaderboard", allEntries = true)
    public void processMatch(MatchRequest request) {

        if (request.player1Id().equals(request.player2Id())) {
            throw new IllegalArgumentException("A member cannot play a match against themselves.");
        }

        Member p1 = repository.findById(request.player1Id())
                .orElseThrow(() -> new NoSuchElementException("Player 1 not found"));

        Member p2 = repository.findById(request.player2Id())
                .orElseThrow(() -> new NoSuchElementException("Player 2 not found"));

        // Update games played for both players
        p1.setGamesPlayed(p1.getGamesPlayed() + 1);
        p2.setGamesPlayed(p2.getGamesPlayed() + 1);

        int rank1 = p1.getRank();
        int rank2 = p2.getRank();

        switch (request.result()) {
            case DRAW:
                // If not adjacent, allow lower-ranked player to move up
                if (Math.abs(rank1 - rank2) > 1) {
                    Member lower = rank1 > rank2 ? p1 : p2;
                    lower.setRank(lower.getRank() - 1);
                }
                break;

            case PLAYER1_WIN:
                updateRanksOnUpset(p1, p2);
                break;

            case PLAYER2_WIN:
                updateRanksOnUpset(p2, p1);
                break;
        }

        repository.save(p1);
        repository.save(p2);
    }

    /**
     * Handles rank shifting when a lower-ranked player beats a higher-ranked one.
     * - Lower-ranked player moves up by half the distance
     * - Higher-ranked player moves down by 1
     */
    private void updateRanksOnUpset(Member lower, Member higher) {
        if (lower.getRank() > higher.getRank()) {
            int diff = lower.getRank() - higher.getRank();
            lower.setRank(lower.getRank() - (diff / 2));
            higher.setRank(higher.getRank() + 1);
        }
    }
}
