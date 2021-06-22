package root.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import root.dto.TagDto;
import root.dto.requests.PostRequest;
import root.dto.responses.TagResponse;
import root.model.Post;
import root.model.Tag;
import root.model.Tag2Post;
import root.repositories.PostRepository;
import root.repositories.Tag2PostRepository;
import root.repositories.TagRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagService {
    private final TagRepository tagRepo;
    private final PostRepository postRepo;
    private final Tag2PostRepository tag2PostRepo;
    private double maxWeight;

    public TagService(TagRepository tagRepo, PostRepository postRepo, Tag2PostRepository tag2PostRepo) {
        this.tagRepo = tagRepo;
        this.postRepo = postRepo;
        this.tag2PostRepo = tag2PostRepo;
        maxWeight = 0;
    }

    /**
     * Подсчёт абсолютного веса каждого тега.
     *
     * @param title имя тега.
     * @return вес.
     */
    private Double weight(String title) {
        int postTotalAmount = postRepo.amountTotal();
        int postWithTheTagAmount = postRepo.amountWithTag(title);
        double weight = (double) postWithTheTagAmount / postTotalAmount;
        if (weight > maxWeight)
            maxWeight = weight;
        return weight;
    }

    /**
     * Получение тегов из базы. В том числе по части имени тега.
     *
     * @param query подстрока для поиска в имени тега.
     * @return ResponseEntity
     */
    public ResponseEntity<TagResponse> getTags(String query) {

        List<Tag> tagList =
                query == null ?
                        tagRepo.findAll() : tagRepo.findAllByTitleStartingWith(query);

        List<TagDto> tags = new ArrayList<>();

        if (!tagList.isEmpty()) {
            HashMap<String, Double> tagWithWeight = new HashMap<>();
            tagList.forEach(t -> tagWithWeight.put(t.getTitle(), weight(t.getTitle())));

            for (Map.Entry<String, Double> entry : tagWithWeight.entrySet()) {
                tags.add(
                        new TagDto(
                                entry.getKey(),
                                entry.getValue() / maxWeight
                        ));
            }
        }
        return ResponseEntity.ok(new TagResponse(tags));
    }


    /**
     * Метод обрабатывает теги из запроса, записывает их в базу и создаёт связи с постом.
     *
     * @param postRequest - пост из запроса.
     * @return - Set(Tag2Post) готовый к записи в пост.
     */
    public Set<Tag2Post> handleTags(PostRequest postRequest, Post newPost) {
        addTagsIfNotExist(postRequest.getTags());
        List<Tag> tags =
                postRequest.getTags()
                        .stream()
                        .map(t -> tagRepo.findByTitle(t))
                        .collect(Collectors.toList());

        Set<Tag2Post> tag2Posts =
                tags.stream()
                        .map(tag -> {
                            Tag2Post t2p = new Tag2Post(newPost, tag);
                            tag2PostRepo.save(t2p);
                            return t2p;
                        }).collect(Collectors.toSet());
        return tag2Posts;
    }


    /**
     * Метод проверяет список тегов на наличие их в базе. Если тега нет, добавляет его.
     *
     * @param tags - список имён тегов.
     */
    private void addTagsIfNotExist(List<String> tags) {
        if (tags.size() != 0) {
            tags.forEach(tag -> {
                if (tagRepo.countAllByTitle(tag.toLowerCase()) == 0)
                    tagRepo.save(new Tag(tag));
            });
        }
    }
}
