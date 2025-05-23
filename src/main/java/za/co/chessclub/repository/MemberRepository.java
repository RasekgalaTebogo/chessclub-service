package za.co.chessclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.chessclub.model.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAllByOrderByRankAsc();

    Member findByRank (int rank);

}
