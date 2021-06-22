package root.controller;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.services.VotesService;

import java.util.Map;

@RestController
@RequestMapping("/api/post")
public class VotesController {
    private final VotesService votesService;

    public VotesController(VotesService votesService) {
        this.votesService = votesService;
    }

    @PostMapping("/like")
    public JSONObject setLike(@RequestBody Map<String, Integer> request) {
        return votesService.setVote((byte) 1, request.get("post_id"));
    }

    @PostMapping("/dislike")
    public JSONObject setDislike(@RequestBody Map<String, Integer> request) {
        return votesService.setVote((byte) -1, request.get("post_id"));
    }
}
