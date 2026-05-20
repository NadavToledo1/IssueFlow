package com.issueflow.mention;

import com.issueflow.comment.Comment;
import com.issueflow.user.User;
import com.issueflow.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MentionService {

    private final MentionRepository repo;
    private final UserRepository userRepo;
    private final MentionParser parser;

    public MentionService(MentionRepository repo,
                          UserRepository userRepo,
                          MentionParser parser) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.parser = parser;
    }

    public List<Mention> getMentions(Comment c) {
        return repo.findByComment(c);
    }

    public void updateMentions(Comment c, String content) {
        repo.deleteByComment(c);

        Set<String> usernames = parser.extractUsernames(content);

        for (String username : usernames) {
            userRepo.findByUsername(username).ifPresent(user -> {
                Mention m = new Mention();
                m.setComment(c);
                m.setUser(user);
                repo.save(m);
            });
        }
    }

    public List<Mention> getMentionsForUser(User user) {
        return repo.findByUserOrderByCommentCreatedAtDesc(user);
    }
}
