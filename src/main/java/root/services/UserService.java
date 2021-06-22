package root.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import root.dto.ErrorsDto;
import root.dto.requests.UserRequest;
import root.dto.responses.MainResponse;
import root.handlers.GlobalHandler;
import root.model.User;
import root.repositories.UserRepository;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final ImageService imageService;
    private final GlobalHandler globalHandler;

    public UserService(UserRepository userRepo, PasswordEncoder encoder, ImageService imageService, GlobalHandler globalHandler) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.imageService = imageService;
        this.globalHandler = globalHandler;
    }

    /**
     * Метод изменяет информацию о пользователе в БД в соответствии с запросом
     *
     * @param request запрос, который может содеражать емэйл, имя, пароль, фото, запрос на удаление фото.
     * @return в случае успеха: MainResponse с true.  В случае ошибок: MainResponse с false и описанием ошибок.
     */
    public MainResponse editProfile(UserRequest request) {
        User user = globalHandler.getUserFromContext();
        if (user == null)
            return new MainResponse(false, "Юзер не авторизован");

        ErrorsDto errorsDto = getErrors(request, user);
        if (errorsDto != null)
            return new MainResponse(false, errorsDto);



        Optional.ofNullable(request.getName()).ifPresent(user::setName);
        Optional.ofNullable(request.getEmail()).ifPresent(user::setEmail);
        Optional.ofNullable(request.getPassword()).ifPresent(p -> user.setPassword(encoder.encode(p)));
        if (request.getPhoto() != null)
            user.setPhoto(fetchAvaPath(user, request.getPhoto()));
        if (request.isRemovePhoto())
            user.setPhoto(null);
        userRepo.save(user);
        return new MainResponse(true);
    }

    /**
     * Метод проверяет есть ли у юзера аватарка на данный момент.
     * Если она есть, передает путь до нее в метод сохранения аватарки на диск.
     * Если её нет, передаёт null.
     *
     * @param user   текущий юзер
     * @param mpFile файл с новой аватаркой
     * @return относительный путь до сохраненной аватарки.
     */
    private String fetchAvaPath(User user, MultipartFile mpFile) {
        String path = null;
        String oldAvaPath = user.getPhoto();
        if (oldAvaPath != null && !oldAvaPath.equals(""))
            path = oldAvaPath.substring(1, oldAvaPath.lastIndexOf("/"));
        return imageService.uploadAvatar(mpFile, path);
    }

    /**
     * Проверка входящих параметров на ошибки
     *
     * @param request объект UserRequest с заполненными или пустыми полями.
     * @param user
     * @return текстовое описание ошибок или null, если ошибок нет.
     */
    private ErrorsDto getErrors(UserRequest request, User user) {
        ErrorsDto errorsDto = new ErrorsDto();
        int errorCount = 0;
        String requestName = request.getName();
        String requestEmail = request.getEmail();
        String requestPassword = request.getPassword();
        MultipartFile mpFile = request.getPhoto();

        if (requestName != null && requestName.length() < 2) {
            errorsDto.setName("Имя должно быть длиннее 1 символа");
            errorCount++;
        }
        if (userRepo.existsByEmail(requestEmail) && !user.getEmail().equals(requestEmail)) {
            errorsDto.setEmail("Этот емэйл уже используется");
            errorCount++;
        }
        if (requestPassword != null && requestPassword.length() < 6) {
            errorsDto.setPassword("Пароль должен быть длиннее 5 символов");
            errorCount++;
        }
        if (mpFile != null && imageService.getError(mpFile) != null) {
            errorsDto.setPhoto(imageService.getError(mpFile));
            errorCount++;
        }
        return errorCount == 0 ? null : errorsDto;
    }

    /**
     * Метод создает UserRequest в случае, когда имеется запрос на удаление фото.
     *
     * @param requestMap Map с данными
     * @return UserRequest с заполненными или пустыми полями.
     */
    public UserRequest mapToUserRequest(Map<String, String> requestMap) {
        UserRequest request = new UserRequest();
        Optional.ofNullable(requestMap.get("name")).ifPresent(request::setName);
        Optional.ofNullable(requestMap.get("password")).ifPresent(request::setPassword);
        Optional.ofNullable(requestMap.get("email")).ifPresent(request::setEmail);
        Optional.ofNullable(requestMap.get("removePhoto")).ifPresent(rf -> request.setRemovePhoto(rf.equals("1")));
        return request;
    }
}
