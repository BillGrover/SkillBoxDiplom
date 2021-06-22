package root.services;

import org.springframework.stereotype.Service;
import root.dto.responses.StatisticsResponse;
import root.handlers.GlobalHandler;
import root.repositories.PostRepository;

@Service
public class StatisticsService {
    private final GlobalHandler globalHandler;
    private final PostRepository postRepo;

    public StatisticsService(GlobalHandler globalHandler, PostRepository postRepo) {
        this.globalHandler = globalHandler;
        this.postRepo = postRepo;
    }

    public StatisticsResponse getMyStatistic() {
        int currentUserId = globalHandler.getUserFromContext().getId();
        return new StatisticsResponse(
                postRepo.countByUserId(currentUserId),
                postRepo.countVotes(currentUserId, (byte) 1),
                postRepo.countVotes(currentUserId, (byte) -1),
                postRepo.countViews(currentUserId),
                postRepo.findFirstPublication(currentUserId).getTime() / 1000
        );
    }

    public StatisticsResponse getAllStatistic() {
        return new StatisticsResponse(
                postRepo.countAll(),
                postRepo.countVotes((byte) 1),
                postRepo.countVotes((byte) -1),
                postRepo.countViews(),
                postRepo.findFirstPublication().getTime() / 1000
        );
    }
}
