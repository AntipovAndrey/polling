package ru.andrey.poll.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.andrey.poll.model.ChoiceVoteCount;
import ru.andrey.poll.model.Vote;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT new ru.andrey.poll.model.ChoiceVoteCount(v.choice.id, COUNT(v.id)) " +
            "FROM Vote v " +
            "WHERE v.poll.id IN :pollIds " +
            "GROUP BY v.choice.id")
    ChoiceVoteCount countByPollIdGroupByChoiceId(@Param("pollId") Long pollId);

    @Query("SELECT new ru.andrey.poll.model.ChoiceVoteCount(v.choice.id, COUNT(v.id)) " +
            "FROM Vote v " +
            "WHERE v.poll.id = :pollIds " +
            "GROUP BY v.choice.id")
    List<ChoiceVoteCount> countByPollIdInGroupByChoiceId(@Param("pollIds") List<Long> pollIds);

    @Query("SELECT v " +
            "FROM Vote v " +
            "WHERE v.user.id = :userId AND v.poll.id = :pollId")
    Vote findByUserIdAndPollId(@Param("userId") Long userId, @Param("pollId") Long pollId);

    @Query("SELECT v " +
            "FROM Vote v " +
            "WHERE v.user.id = :userId AND v.poll.id IN :pollId")
    List<Vote> findByUserIdAndPollIdIn(@Param("userId") Long userId, @Param("pollIds") List<Long> pollIds);

    @Query("SELECT COUNT(v.id) " +
            "FROM Vote v " +
            "WHERE v.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT v.poll.id " +
            "FROM Vote v " +
            "WHERE v.user.id = :userId")
    Page<Long> findVotedPollIdsByUserId(@Param("userId") Long userId, Pageable pageable);
}
