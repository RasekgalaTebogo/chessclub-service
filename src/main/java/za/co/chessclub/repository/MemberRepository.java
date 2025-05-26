package za.co.chessclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.chessclub.model.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Returns all members ordered by their rank in ascending order.
     * Useful for the leaderboard view and to keep consistent rank logic.
     */
    List<Member> findAllByOrderByRankAsc();

    /**
     * Finds a member by their rank.
     * This is useful if you want to shift or update rank positions.
     */
    Member findByRank(int rank);

    List<Member> findByRankBetween(int start, int end);

}
