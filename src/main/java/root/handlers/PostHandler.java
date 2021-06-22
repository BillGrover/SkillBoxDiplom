package root.handlers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import root.dto.ErrorsDto;
import root.dto.requests.PostRequest;
import root.dto.responses.PostsResponse;
import root.model.enums.ModerationStatus;
import root.model.enums.PostStatus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class PostHandler {
    /**
     * Метод парсит входящую строку в целое число.
     *
     * @param str входная строка.
     * @return 0, если строка пуста;
     * -1, если парсинг не удался;
     * целочисленное значение, в случае успешного парсинга.
     */
    public int parseInt(String str) {
        if (str == null)
            return 0;

        int result = -1;
        try {
            result = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            System.out.println("PostHandler.parseInt(). Успешно обработано исключение: NumberFormatException");
        }
        return result;
    }

    /**
     * Метод создаёт response с описанием ошибки.
     *
     * @param error описание ошибки.
     * @return MainResponse и HttpStatus.BAD_REQUEST
     */
    public ResponseEntity<PostsResponse> badRequest(String error) {
        System.out.println("PostHandler.badRequest(): " + error);
        return new ResponseEntity<>(new PostsResponse(false, new ErrorsDto(error)), HttpStatus.BAD_REQUEST);
    }

    public Date parseDate(String dateStr, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        Date result = null;
        try {
            result = df.parse(dateStr);
        } catch (ParseException e) {
            System.out.println("PostHandler.parseDate(). Успешно обработано исключение: ParseException");
        }
        return result;
    }

    /**
     * Проверка параметров на наличие ошибок.
     *
     * @param offset параметр из запроса.
     * @param limit  параметр из запроса.
     * @return строковое описание ошибки или null, если ошибок нет.
     */
    public String getError(int offset, int limit) {
        String error = null;
        if (offset < 0)
            error = "Неверный параметр offset в запросе";
        else if (limit < 0)
            error = "Неверный параметр limit в запросе";
        return error;
    }

    /**
     * Проверка параметров на наличие ошибок.
     *
     * @param offset параметр из запроса.
     * @param limit  параметр из запроса.
     * @param date   параметр из запроса.
     * @return строковое описание ошибки или null, если ошибок нет.
     */
    public String getError(int offset, int limit, Date date) {
        String error = getError(offset, limit);
        if (date == null)
            error = "Неверный параметр date в запросе";
        return error;
    }

    /**
     * Проверка параметров на наличие ошибок.
     *
     * @param offset параметр из запроса.
     * @param limit  параметр из запроса.
     * @param status параметр из запроса.
     * @return строковое описание ошибки или null, если ошибок нет.
     */
    public String getError(int offset, int limit, String status, String statusOf) {
        String error = getError(offset, limit);
        if (statusOf.equals("moderation") && !ModerationStatus.exists(status))
            error = "Неверно указан статус модерации.";
        if (statusOf.equals("post") && !PostStatus.exists(status))
            error = "Неверно указан статус поста.";
        return error;
    }

    public String getError(PostRequest postRequest) {
        String error = null;
        if (postRequest.getText().length() < 50)
            error = "Длина текста поста не должна быть менее 50 символов";
        else if (postRequest.getTitle().length() < 3)
            error = "Длина заголовка поста не должна быть менее 3 символов";
        return error;
    }
}
