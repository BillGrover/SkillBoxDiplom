package root.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import root.dto.ErrorsDto;
import root.dto.requests.ModerationRequest;
import root.dto.requests.PostRequest;
import root.dto.responses.MainResponse;
import root.dto.responses.OnePostResponse;
import root.dto.responses.PostsResponse;
import root.handlers.GlobalHandler;
import root.handlers.PostHandler;
import root.model.Post;
import root.model.User;
import root.model.enums.ModerationStatus;
import root.repositories.*;

import java.util.Date;

@Service
public class PostService {

    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final TagRepository tagRepo;
    private final Tag2PostRepository tag2PostRepo;
    private final GlobalSettingsRepository globalSettingsRepo;
    private final TagService tagService;
    private final PostHandler handler;
    private final GlobalHandler globalHandler;

    public PostService(PostRepository postRepo, UserRepository userRepo, TagRepository tagRepo,
                       Tag2PostRepository tag2PostRepo, GlobalSettingsRepository globalSettingsRepo, TagService tagService,
                       PostHandler handler, GlobalHandler globalHandler) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.tagRepo = tagRepo;
        this.tag2PostRepo = tag2PostRepo;
        this.globalSettingsRepo = globalSettingsRepo;
        this.tagService = tagService;
        this.handler = handler;
        this.globalHandler = globalHandler;
    }

    /**
     * Метод получения постов со всей сопутствующей информацией для главной страницы и подразделов "Новые", "Самые обсуждаемые", "Лучшие" и "Старые".
     * Метод выводит посты, отсортированные в соответствии с параметром mode.
     *
     * @param offsetStr Offset for pagination.
     * @param limitStr  Limit  for pagination.
     * @param mode      recent/popular/best/early
     * @return ResponseEntity
     */
    public ResponseEntity<PostsResponse> getPosts(String offsetStr, String limitStr, String mode) {
        int offset = handler.parseInt(offsetStr);
        int limit = handler.parseInt(limitStr);

        String error = handler.getError(offset, limit);
        if (error != null)
            return handler.badRequest(error);

        Pageable pagination = PageRequest.of(offset / limit, limit);
        Page<Post> posts;
        switch (mode) {
            case ("recent"):    //сначала новые
                posts = postRepo.findRecent(pagination);
                break;
            case ("popular"):   //по убыванию комментариев (comments.size)
                posts = postRepo.findPopular(pagination);
                break;
            case ("best"):      //по убыванию лайков (сумма значений листа votes)
                posts = postRepo.findBest(pagination);
                break;
            case ("early"):     //сначала старые
                posts = postRepo.findEarly(pagination);
                break;
            default:            //по id
                posts = postRepo.findAllByIsActiveAndModerationStatusAndTimeBeforeOrderById(
                        (byte) 1, ModerationStatus.ACCEPTED, new Date(), pagination);
        }
        return ResponseEntity.ok(new PostsResponse(posts));
    }

    /**
     * Метод возвращает посты, соответствующие поисковому запросу - строке query.
     * В случае, если запрос пустой, метод выводит все посты.
     *
     * @param offsetStr Offset for pagination.
     * @param limitStr  Limit  for pagination.
     * @param query     Query for search
     * @return ResponseEntity
     */
    public ResponseEntity<PostsResponse> getByQuery(String offsetStr, String limitStr, String query) {
        int offset = handler.parseInt(offsetStr);
        int limit = handler.parseInt(limitStr);

        String error = handler.getError(offset, limit);
        if (error != null)
            return handler.badRequest(error);

        Pageable pagination = PageRequest.of(offset / limit, limit);

        Page<Post> posts;
        if (query == null || query.matches("\\s+"))
            posts = postRepo.findAllByIsActiveAndModerationStatusAndTimeBeforeOrderById(
                    (byte) 1, ModerationStatus.ACCEPTED, new Date(), pagination);
        else {
            posts = postRepo.findByQuery("%" + query + "%", pagination);
        }
        return ResponseEntity.ok(new PostsResponse(posts));
    }


    /**
     * Выводит посты за указанную дату, переданную в запросе в параметре date.
     *
     * @param offsetStr Offset for pagination.
     * @param limitStr  Limit  for pagination.
     * @param dateStr   The day for search.
     * @return ResponseEntity
     */
    public ResponseEntity<PostsResponse> getByDate(String offsetStr, String limitStr, String dateStr) {
        int offset = handler.parseInt(offsetStr);
        int limit = handler.parseInt(limitStr);
        Date startOfTheDay = handler.parseDate(dateStr, "yyyy-MM-dd");
        Date endOfTheDay = handler.parseDate(dateStr + "-23-59-59-999", "yyyy-MM-dd-hh-mm-ss-SSS");

        String error = handler.getError(offset, limit, startOfTheDay);
        if (error != null)
            return handler.badRequest(error);

        Page<Post> posts = postRepo.findAllByIsActiveAndModerationStatusAndTimeBeforeAndTimeBetweenOrderByTimeDesc(
                (byte) 1, ModerationStatus.ACCEPTED, new Date(), startOfTheDay, endOfTheDay, PageRequest.of(offset / limit, limit));
        return ResponseEntity.ok(new PostsResponse(posts));
    }

    /**
     * Метод выводит список постов, привязанных к тэгу, который был передан методу в качестве параметра tag.
     *
     * @param offsetStr Offset for pagination.
     * @param limitStr  Limit  for pagination.
     * @param tag       Tag name for search.
     * @return ResponseEntity.
     */
    public ResponseEntity<PostsResponse> getByTag(String offsetStr, String limitStr, String tag) {
        int offset = handler.parseInt(offsetStr);
        int limit = handler.parseInt(limitStr);

        String error = handler.getError(offset, limit);
        if (error != null)
            return handler.badRequest(error);

        Page<Post> posts = postRepo.findbyTag(tag, PageRequest.of(offset / limit, limit));
        return ResponseEntity.ok(new PostsResponse(posts));
    }

    /**
     * Метод выводит все посты, которые требуют модерационных действий (которые нужно утвердить или отклонить)
     * или над которыми мною были совершены модерационные действия: которые я отклонил или утвердил.
     *
     * @param offsetStr Offset for pagination.
     * @param limitStr  Limit  for pagination.
     * @param status    Moderation status.
     * @return ResponseEntity.
     */
    public ResponseEntity<PostsResponse> getModeration(String offsetStr, String limitStr, String status) {
        int offset = handler.parseInt(offsetStr);
        int limit = handler.parseInt(limitStr);

        String error = handler.getError(offset, limit, status, "moderation");
        if (error != null)
            return handler.badRequest(error);

        Pageable pagination = PageRequest.of(offset / limit, limit);

        Page<Post> posts =
                status.toUpperCase().equals("NEW") ?
                        postRepo.findByStatusAndModeratorId(status.toUpperCase(), pagination) :
                        postRepo.findByStatusAndModeratorId(status.toUpperCase(), globalHandler.getUserFromContext().getId(), pagination);

        return ResponseEntity.ok(new PostsResponse(posts));
    }

    /**
     * Метод выводит только те посты, которые создал текущий юзер.
     *
     * @param offsetStr Offset for pagination.
     * @param limitStr  Limit  for pagination.
     * @param status    Moderation status:
     *                  inactive - скрытые, ещё не опубликованы (is_active = 0);
     *                  pending - активные, ожидают утверждения модератором (is_active = 1, moderation_status = NEW);
     *                  declined - отклонённые по итогам модерации (is_active = 1, moderation_status = DECLINED);
     *                  published - опубликованные по итогам модерации (is_active = 1, moderation_status = ACCEPTED).
     * @return ResponseEntity.
     */
    public ResponseEntity<PostsResponse> getMy(String offsetStr, String limitStr, String status) {
        int offset = handler.parseInt(offsetStr);
        int limit = handler.parseInt(limitStr);
        Pageable pagination = PageRequest.of(offset / limit, limit);
        int currentUserId = globalHandler.getUserFromContext().getId();

        String error = handler.getError(offset, limit, status, "post");
        if (error != null)
            return handler.badRequest(error);

        Page<Post> posts;
        switch (status.toUpperCase()) {
            case ("INACTIVE"):
                posts = postRepo.findInactive(currentUserId, pagination);
                break;
            case ("PENDING"):
                posts = postRepo.findByStatusAndUserId(currentUserId, "NEW", pagination);
                break;
            case ("DECLINED"):
                posts = postRepo.findByStatusAndUserId(currentUserId, "DECLINED", pagination);
                break;
            case ("PUBLISHED"):
                posts = postRepo.findByStatusAndUserId(currentUserId, "ACCEPTED", pagination);
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new PostsResponse(posts));
    }

    /**
     * Метод выводит данные конкретного поста для отображения на странице поста,
     * в том числе, список комментариев и тэгов, привязанных к данному посту.
     * Если вызывающий этот метод юзер - не модератор и не автор поста, увеличивает количество просмотров.
     *
     * @param id - id поста.
     * @return ResponseEntity.
     */
    public ResponseEntity<OnePostResponse> getOnePost(int id) {
        User user = globalHandler.getUserFromContext();
        Post post;

        if (user != null && user.getIsModerator() == 0)
            post = postRepo.findFiltredByIdOrByUserId(id, user.getId());
        else if (user != null && user.getIsModerator() == 1)
            post = postRepo.findFiltredByIdOrByModeratorId(id, user.getId());
        else
            post = postRepo.findFiltredById(id);

        if (post == null)
            return new ResponseEntity<>(
                    new OnePostResponse(false, new ErrorsDto("Пост с id = " + id + " не найден.")),
                    HttpStatus.NOT_FOUND);

        //viewCount incrementation
        if (user != null && user.getIsModerator() != 1 && post.getUser().getId() != user.getId()) {
            post.setViewCount(post.getViewCount() + 1);
            postRepo.save(post);
        }
        return ResponseEntity.ok(new OnePostResponse(post));
    }

    /**
     * Метод сохраняет новый пост в БД. Возвращает ошибки, если они есть.
     *
     * @param postRequest - Данные поста, которые нужно записать в БД.
     * @return MainResponse.
     */
    public ResponseEntity<PostsResponse> savePost(PostRequest postRequest) {
        String error = handler.getError(postRequest);
        if (error != null)
            return ResponseEntity.ok(new PostsResponse(false, new ErrorsDto(error)));

        try {
            Post newPost = postRequest.createPost();
            newPost.setModerationStatus(dependsOfGlobalSettings());
            newPost.setUser(globalHandler.getUserFromContext());
            postRepo.save(newPost);
            newPost.setTag2Post(tagService.handleTags(postRequest, newPost));
        } catch (Exception e) {
            error = "Непредвиденная ошибка при создании и записи нового поста в базу.";
            return ResponseEntity.ok(new PostsResponse(false, new ErrorsDto(error)));
        }
        return ResponseEntity.ok(new PostsResponse(true, new ErrorsDto()));
    }

    /**
     * Метод апдейтит пост или возвращает ошибки, если апдейт не удался.
     *
     * @param id          - id поста
     * @param postRequest - новые данные поста.
     * @return MainResponse.
     */
    public ResponseEntity<PostsResponse> updatePost(int id, PostRequest postRequest) {
        String error = handler.getError(postRequest);
        if (error != null)
            return handler.badRequest(error);

        try {
            Post post = postRepo.findByIdOnly(id);
            post = postRequest.updatePost(post);
            if (globalHandler.getUserFromContext().getIsModerator() == 0)
                post.setModerationStatus(dependsOfGlobalSettings());
            post.setTag2Post(tagService.handleTags(postRequest, post));
            postRepo.save(post);
        } catch (Exception e) {
            error = "Ошибка при редактировании поста в базе данных";
            return handler.badRequest(error);
        }
        return ResponseEntity.ok(new PostsResponse(true, new ErrorsDto()));
    }

    /**
     * Метод изменяет статус модерации поста.
     *
     * @param request реквест с id поста и новым статусом меодерации.
     * @return MainResponse (result = true или false)
     */
    public ResponseEntity<MainResponse> moderatePost(ModerationRequest request) {
        int postId = request.getPostId();
        String status = request.getDecision();
        User currentUser = globalHandler.getUserFromContext();

        //Проверка на ошибки
        String error = null;
        if (!postRepo.existsById(postId)) {
            error = "PostService.moderatePost():\n Пост с ID = " + postId + " не найден.";
        } else if (status == null) {
            error = "PostService.moderatePost():\n Не указан статус модерации.";
        } else if (!status.equals("decline") && !status.equals("accept")) {
            error = "PostService.moderatePost():\n Неверный статус модерации.";
        }
        if (error != null)
            return new ResponseEntity<>(new MainResponse(false, error), HttpStatus.BAD_REQUEST);

        Post post = postRepo.findByIdOnly(postId);
        post.setModerationStatus(
                status.equals("decline") ? ModerationStatus.DECLINED : ModerationStatus.ACCEPTED);
        post.setModerator(currentUser);
        postRepo.save(post);

        return ResponseEntity.ok(new MainResponse(true));
    }

    /**
     * Метод проверяет глобальные настройки.
     * Если премодерация активна, устанавливает статус новых постов - NEW, в противном случае - ACCEPTED.
     *
     * @return ModerationStatus.NEW or ModerationStatus.ACCEPTED.
     */
    private ModerationStatus dependsOfGlobalSettings() {
        if (globalSettingsRepo.findByCode("POST_PREMODERATION").getValue().equals("YES"))
            return ModerationStatus.NEW;
        else
            return ModerationStatus.ACCEPTED;
    }
}
