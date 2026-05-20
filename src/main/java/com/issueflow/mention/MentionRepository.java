package com.issueflow.mention;

import com.issueflow.comment.Comment;
import com.issueflow.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentionRepository extends JpaRepository<Mention, Long> {

    List<Mention> findByComment(Comment comment);

    List<Mention> findByUserOrderByCommentCreatedAtDesc(User user);

    void deleteByComment(Comment comment);
}
