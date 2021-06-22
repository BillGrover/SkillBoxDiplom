package root.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import root.model.PostVote;

public interface VotesRepository extends JpaRepository<PostVote, Long> {
    @Query(value = "FROM PostVote v " +
            "WHERE v.user.id = :userId " +
            "AND v.post.id = :postId " +
            "AND v.value = :vote")
    PostVote findByUserAndPost(@Param("userId") int id, @Param("postId") Integer postId, @Param("vote") byte vote);
}
