package root.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import root.dto.CalendarDto;
import root.model.Post;
import root.model.enums.ModerationStatus;

import java.util.Date;
import java.util.List;

public interface PostRepository extends PagingAndSortingRepository<Post, Integer> {

    /**
     * Поиск постов: активных, допущенных(ACCEPTED), неотложенных.
     *
     * @param isActive         - активен ли пост?
     * @param moderationStatus - статус модерации поста.
     * @param time             - время публикации поста.
     * @param p                - пагинация.
     * @return посты в виде Page<Post>.
     */
    Page<Post> findAllByIsActiveAndModerationStatusAndTimeBeforeOrderById(
            byte isActive, ModerationStatus moderationStatus, Date time, Pageable p);

    /**
     * Метод ищет посты, у которых заголовок содержит строку query.
     *
     * @param query - строка из запроса.
     * @param p     пагинация.
     * @return посты в виде Page<Post>.
     */
    @Query("FROM Post p " +
            "WHERE p.isActive = 1 " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIME " +
            "AND p.title LIKE :query " +
            "ORDER BY p.time DESC")
    Page<Post> findByQuery(@Param("query") String query, Pageable p);

    /**
     * Метод находит пост по id.
     *
     * @return Post
     */
    @Query(value = "FROM Post p WHERE p.id = :id")
    Post findByIdOnly(@Param("id") int id);

    /**
     * Метод находит пост активный, допущенный, неотложенный по id.
     *
     * @return Post
     */
    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = 1 " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIME " +
            "AND p.id = :id")
    Post findFiltredById(@Param("id") int id);

    /**
     * Метод находит пост активный, неотложенный по id.
     *
     * @return Post
     */
    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = 1 " +
            "AND p.time < CURRENT_TIME " +
            "AND p.id = :id")
    Post findNewById(@Param("id") int id);

    /**
     * Поиск самых новых постов.
     *
     * @param p пагинация.
     * @return посты в виде Page<Post>.
     */
    @Query("SELECT p FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < CURRENT_TIME " +
            "ORDER BY p.time DESC")
    Page<Post> findRecent(Pageable p);

    /**
     * Поиск самых старых постов
     *
     * @param p пагинация.
     * @return посты в виде Page<Post>.
     */
    @Query("SELECT p FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < CURRENT_TIME " +
            "ORDER BY p.time ASC")
    Page<Post> findEarly(Pageable p);

    /**
     * Поиск постов с наибольшим количеством комментариев.
     *
     * @param p пагинация.
     * @return посты в виде Page<Post>.
     */
    @Query(value = "SELECT * FROM posts p " +
            "JOIN (SELECT post_id, count(*) comments_amount FROM post_comments GROUP BY post_id) nt " +
            "ON p.id = nt.post_id " +
            "WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' AND p.time < CURRENT_TIME " +
            "ORDER BY nt.comments_amount DESC",
            nativeQuery = true)
    Page<Post> findPopular(Pageable p);

    /**
     * Поиск постов с наибольшим количеством лайков.
     *
     * @param p пагинация.
     * @return посты в виде Page<Post>.
     */
    @Query(value = "SELECT * FROM posts p " +
            "JOIN (SELECT post_id, count(*) likes_amount FROM post_votes  WHERE value > 0 GROUP BY post_id) nt " +
            "ON p.id = nt.post_id " +
            "WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' AND p.time < CURRENT_TIME " +
            "ORDER BY nt.likes_amount DESC",
            nativeQuery = true)
    Page<Post> findBest(Pageable p);

    /**
     * Поиск допущенных постов за определенный день.
     *
     * @param isActive активен ли пост.
     * @param ms       статус модерации поста.
     * @param time     дата поста.
     * @param d1       начальная дата для поиска.
     * @param d2       конечноая дата для поиска.
     * @param p        пагинация.
     * @return посты в виде Page<Post>.
     */
    Page<Post> findAllByIsActiveAndModerationStatusAndTimeBeforeAndTimeBetweenOrderByTimeDesc(
            byte isActive, ModerationStatus ms, Date time, Date d1, Date d2, Pageable p);

    /**
     * Поиск постов по тегу.
     *
     * @param tag тег.
     * @param p   пагинация.
     * @return посты в виде Page<Post>.
     */
    @Query(value = "SELECT * FROM posts " +
            "JOIN tag2post ON posts.id = tag2post.post_id " +
            "JOIN tags ON tag2post.tag_id = tags.id " +
            "WHERE posts.is_active = 1 " +
            "AND posts.moderation_status = 'ACCEPTED' " +
            "AND posts.time < CURRENT_TIME " +
            "AND tags.title = :tag",
            nativeQuery = true)
    Page<Post> findbyTag(@Param("tag") String tag, Pageable p);

    /**
     * Поиск постов по статусу.
     *
     * @param status статус модерации.
     * @param p      пагинация.
     * @return посты в виде Page<Post>.
     */
    @Query(value = "SELECT * FROM posts " +
            "WHERE posts.is_active = 1 " +
            "AND posts.moderation_status = :status ",
            nativeQuery = true)
    Page<Post> findByStatusAndModeratorId(@Param("status") String status, Pageable p);

    /**
     * Поиск постов, проверенных определённым модератором и статусу поста.
     *
     * @param status  - отклонённые (DECLINED) или принятые (ACCEPTED) посты.
     * @param moderId - id модератора.
     * @param p       - Параметры пагинации.
     * @return Page.
     */
    @Query(value = "SELECT * FROM posts " +
            "WHERE posts.is_active = 1 " +
            "AND posts.moderation_status = :status " +
            "AND posts.moderator_id = :moderId",
            nativeQuery = true)
    Page<Post> findByStatusAndModeratorId(@Param("status") String status, @Param("moderId") int moderId, Pageable p);

    /**
     * Поиск неопубликованных постов текущего юзера.
     *
     * @param currentUserId - ID текущего авторизованного юзера.
     * @param p             - Параметры пагинации.
     * @return Page.
     */
    @Query(value = "SELECT * FROM posts WHERE user_id = :currentUserId AND is_active = 0",
            nativeQuery = true)
    Page<Post> findInactive(@Param("currentUserId") int currentUserId, Pageable p);

    /**
     * Поиск постов текущего юзера, ожидающих модерации (NEW), отклонённых модератором (DECLINED), опубликованных (ACCEPTED).
     *
     * @param currentUserId - ID текущего авторизованного юзера.
     * @param p             - Параметры пагинации.
     * @return посты в виде Page<Post>.
     */
    @Query(value = "SELECT * FROM posts WHERE user_id = :currentUserId AND moderation_status = :status AND is_active = 1",
            nativeQuery = true)
    Page<Post> findByStatusAndUserId(@Param("currentUserId") int currentUserId, @Param("status") String status, Pageable p);

    /**
     * Поиск количества постов с определенным тегом.
     *
     * @param tag тег.
     * @return посты в виде Page<Post>.
     */
    @Query(value = "SELECT COUNT(*) FROM posts " +
            "JOIN tag2post ON posts.id = tag2post.post_id " +
            "JOIN tags ON tag2post.tag_id = tags.id " +
            "WHERE posts.is_active = 1 " +
            "AND posts.moderation_status = 'ACCEPTED' " +
            "AND posts.time < CURRENT_TIME " +
            "AND tags.title = :tag",
            nativeQuery = true)
    int amountWithTag(@Param("tag") String tag);

    /**
     * Поиск количества постов.
     *
     * @return число постов.
     */
    @Query(value = "SELECT COUNT(*) FROM posts " +
            "WHERE posts.is_active = 1 " +
            "AND posts.moderation_status = 'ACCEPTED' " +
            "AND posts.time < CURRENT_TIME ",
            nativeQuery = true)
    int amountTotal();

    /**
     * Поиск количества постов по статусу модерации.
     *
     * @param status статус модерации.
     * @return посты в виде Page<Post>.
     */
    @Query(value = "SELECT COUNT(*) FROM posts " +
            "WHERE posts.moderation_status = :status " +
            "AND posts.time < CURRENT_TIME " +
            "AND posts.is_active = 1",
            nativeQuery = true)
    int countByModerationStatus(@Param("status") String status);

    /**
     * Метод возвращает список годов в которые был опубликован хотя бы один пост: допущенный, активный, с датой не позднее текущего момента.
     *
     * @return список годов в виде четырехзначных чисел.
     */
    @Query(value = "SELECT YEAR(p.time) AS years FROM posts p GROUP BY years",
            nativeQuery = true)
    List<Integer> findYearsWithPosts();

    /**
     * Метод ищет в в базе посты за год, указанный в параметре year.
     * Затем формирует из них список объектов с двумя полями: дата и количество постов за эту дату.
     *
     * @param year год
     * @return список объектов CalendarDto
     */
    @Query(value = "SELECT " +
            "new root.dto.CalendarDto((function('DATE_FORMAT', p.time, '%Y-%m-%d')), count(p)) " +
            "FROM Post p " +
            "WHERE p.isActive = 1 " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIME " +
            "AND function('YEAR', p.time) = :year " +
            "GROUP BY (function('DATE_FORMAT', p.time, '%Y-%m-%d'))")
    List<CalendarDto> findCount2Date(@Param("year") int year);

    int countByUserId(int userId);

    @Query(value = "SELECT COUNT(p) FROM Post p")
    int countAll();

    @Query(value = "SELECT SUM(p.viewCount) FROM Post p WHERE p.user.id = :id")
    int countViews(@Param("id") int userId);

    @Query(value = "SELECT SUM(p.viewCount) FROM Post p")
    int countViews();

    @Query(value = "SELECT COUNT(v) FROM PostVote v WHERE v.post.user.id = :id AND v.value = :val")
    int countVotes(@Param("id") int userId, @Param("val") byte likeOrDislike);

    @Query(value = "SELECT COUNT(v) FROM PostVote v WHERE v.value = :val")
    int countVotes(@Param("val") byte likeOrDislike);

    @Query(value = "SELECT MIN(p.time) FROM Post p WHERE p.user.id = :id")
    Date findFirstPublication(@Param("id") int userId);

    @Query(value = "SELECT MIN(p.time) FROM Post p")
    Date findFirstPublication();

    /**
     * Метод находит пост по заданным id поста и юзера (не модератора)
     * Если такой пост находится (этот юзер - автор этого поста), то этот пост возвращается как есть.
     * Если такого поста нет (этот юзер - не автор этого поста), то этот пост возвращается, если активен, допущен и не отложен.
     * @param id id поста
     * @param userId id юзера
     * @return объект Post
     */
    @Query(value = "SELECT p FROM Post p " +
            "WHERE (p.id = :postId AND p.user.id = :userId) " +
            "OR (p.id = :postId AND NOT (p.user.id = :userId) AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < CURRENT_TIME)")
    Post findFiltredByIdOrByUserId(@Param("postId") int id, @Param("userId") int userId);

    /**
     * Метод находит пост по заданным id поста и юзера (модератора)
     * Если такой пост находится (этот юзер - автор этого поста), то этот пост возвращается как есть.
     * Если такого поста нет (этот юзер - не автор этого поста), то этот пост возвращается, если активен и не отложен.
     * @param id id поста
     * @param moderId id юзера
     * @return объект Post
     */
    @Query(value = "SELECT p FROM Post p " +
            "WHERE (p.id = :postId AND p.user.id = :moderId) " +
            "OR (p.id = :postId AND NOT (p.user.id = :moderId) AND p.isActive = 1 AND p.time < CURRENT_TIME)")
    Post findFiltredByIdOrByModeratorId(@Param("postId") int id, @Param("moderId") int moderId);
}
