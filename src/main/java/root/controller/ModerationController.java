package root.controller;

import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.dto.requests.ModerationRequest;
import root.dto.responses.MainResponse;
import root.services.PostService;

@RestController
@RequestMapping("/api/moderation")
public class ModerationController {

    private final PostService postService;

    public ModerationController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<MainResponse> moderatePost(@RequestBody ModerationRequest request) throws NotFoundException {
        return postService.moderatePost(request);
    }

}
