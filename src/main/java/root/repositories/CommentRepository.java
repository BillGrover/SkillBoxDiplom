package root.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import root.model.PostComment;

public interface CommentRepository extends JpaRepository<PostComment, Integer> {
}
