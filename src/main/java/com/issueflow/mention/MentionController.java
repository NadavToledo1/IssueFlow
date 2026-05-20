package com.issueflow.mention;

import com.issueflow.comment.dto.CommentResponse;
import com.issueflow.user.User;
import com.issueflow.user.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/mentions")
public class MentionController {

    private final MentionService mentionService;
    private final UserService userService;

    public MentionController(MentionService mentionService, UserService userService) {
        this.mentionService = mentionService;
        this.userService = userService;
    }

    @GetMapping
    public List<CommentResponse> getMentions(@PathVariable Long userId) {
        User u = userService.loadEntity(userId);
        return mentionService.getMentionsForUser(u).stream()
                .map(m -> CommentResponse.from(m.getComment(), mentionService.getMentions(m.getComment())))
                .toList();
    }
}
