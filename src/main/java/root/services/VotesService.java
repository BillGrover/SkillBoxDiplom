package root.services;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import root.handlers.GlobalHandler;
import root.model.Post;
import root.model.PostVote;
import root.model.User;
import root.repositories.PostRepository;
import root.repositories.VotesRepository;

import java.util.Collections;

@Service
public class VotesService {
    private final PostRepository postRepo;
    private final GlobalHandler globalHandler;
    private final VotesRepository votesRepo;

    public VotesService(PostRepository postRepo, GlobalHandler globalHandler, VotesRepository votesRepo) {
        this.postRepo = postRepo;
        this.globalHandler = globalHandler;
        this.votesRepo = votesRepo;
    }

    public JSONObject setVote(byte vote, int postId) {
        PostVote newVote;
        User user = globalHandler.getUserFromContext();
        Post post = postRepo.findByIdOnly(postId);
        if (post == null)
            return new JSONObject(Collections.singletonMap("result", false));

        if (votesRepo.findByUserAndPost(user.getId(), postId, vote) != null) {
            return new JSONObject(Collections.singletonMap("result", false));
        } else if (votesRepo.findByUserAndPost(user.getId(), postId, (byte) (-1 * vote)) != null) {
            newVote = votesRepo.findByUserAndPost(user.getId(), postId, (byte) (-1 * vote));
            newVote.setValue(vote);
            votesRepo.saveAndFlush(newVote);
        } else {
            newVote = new PostVote(user, post, vote);
            votesRepo.saveAndFlush(newVote);
        }
        post.addVote(newVote);
        postRepo.save(post);
        return new JSONObject(Collections.singletonMap("result", true));
    }
}
