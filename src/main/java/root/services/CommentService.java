package root.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import root.dto.ErrorsDto;
import root.dto.requests.CommentRequest;
import root.dto.responses.CommentResponse;
import root.handlers.GlobalHandler;
import root.model.PostComment;
import root.repositories.CommentRepository;
import root.repositories.PostRepository;

import java.util.Date;

@Service
public class CommentService {

    private final CommentRepository commentRepo;
    private final PostRepository postRepo;
    private final PostService postService;
    private final GlobalHandler globalHandler;

    public CommentService(CommentRepository commentRepo, PostRepository postRepo, PostService postService, GlobalHandler globalHandler) {
        this.commentRepo = commentRepo;
        this.postRepo = postRepo;
        this.postService = postService;
        this.globalHandler = globalHandler;
    }

    /**
     * Метод создаёт новый комментарий в базе на основе данных из реквеста.
     * @param request реквест.
     * @return CommentResponse и HttpStatus.OK либо MainResponse и HttpStatus.BAD_REQUEST
     */
    public ResponseEntity<CommentResponse> postComment(CommentRequest request) {

        int parentCommentId = fetchParentId(request);
        int postId = request.getPostId();
        String text = request.getText();

        //Проверка входящих параметров
        String error = null;
        if (text == null || text.length() < 3)
            error = "Текст не задан или слишком короткий.";
        else if (!postRepo.existsById(postId))
            error = "Запрошенный пост не найден.";
        else if (parentCommentId > 0 && !commentRepo.existsById(parentCommentId))
            error = "Запрошенный родительский комментарий не найден.";

        if (error != null)
            return badRequest(error);

        //Создание комментария
        PostComment comment = new PostComment();
        comment.setPost(postRepo.findFiltredById(postId));
        comment.setText(text);
        comment.setTime(new Date());
        comment.setUser(globalHandler.getUserFromContext());
        if (parentCommentId > 0)
            comment.setParentId(parentCommentId);
        commentRepo.saveAndFlush(comment);

        return ResponseEntity.ok(new CommentResponse(comment.getId()));
    }

    /**
     * Метод проверяет наличие и корректность id родительского комментария в запросе.
     *
     * @param request запрос.
     * @return null, если id родительский комментарий не указан;
     * -1, если родительский комментарий не является числом;
     * целочисленное значение, если родительский комментарий запрошен верно.
     */
    private int fetchParentId(CommentRequest request) {
        String parentId = request.getParentId();

        if (parentId == null || parentId.equals(""))
            return 0;

        int resultParentId = -1;
        try {
            resultParentId = Integer.parseInt(parentId);
        } catch (NumberFormatException e) {
            System.out.println("CommentService.fetchParentId(). Успешно обработано исключение: NumberFormatException\n" +
                    "Было: resultParentId = " + parentId + ". Стало: resultParentId = " + resultParentId);
        }
        return resultParentId;
    }


    /**
     * Метод создаёт response с описанием ошибки.
     *
     * @param s описание ошибки.
     * @return CommentResponse и HttpStatus.BAD_REQUEST
     */
    private ResponseEntity<CommentResponse> badRequest(String s) {
        System.out.println("CommentService.badRequest(): " + s);
        return new ResponseEntity<>(
                new CommentResponse(
                        false,
                        new ErrorsDto(s)),
                HttpStatus.BAD_REQUEST);
    }
}
