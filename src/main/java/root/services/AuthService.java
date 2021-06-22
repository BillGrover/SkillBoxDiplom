package root.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import root.dto.ErrorsDto;
import root.dto.UserDto;
import root.dto.requests.NewPasswordRequest;
import root.dto.requests.UserRequest;
import root.dto.responses.LoginResponse;
import root.dto.responses.MainResponse;
import root.model.CaptchaCode;
import root.model.Role;
import root.model.Role2User;
import root.repositories.CaptchaCodeRepository;
import root.repositories.PostRepository;
import root.repositories.UserRepository;
import root.repositories.UsersRolesRepository;
import root.security.SecurityUser;


@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepo;
    private final PostRepository postRepo;
    private final CaptchaCodeRepository captchaCodeRepo;
    private final CaptchaService captchaService;
    private final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder(12);
    private final UsersRolesRepository usersRolesRepo;
    private final MailService emailService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepo,
            PostRepository postRepo,
            CaptchaCodeRepository captchaCodeRepo, CaptchaService captchaService,
            UsersRolesRepository usersRolesRepo, MailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.postRepo = postRepo;
        this.captchaCodeRepo = captchaCodeRepo;
        this.captchaService = captchaService;
        this.usersRolesRepo = usersRolesRepo;
        this.emailService = emailService;
    }

    /**
     * Метод логина юзера
     *
     * @param request данные, которые юзер вводит на сайте.
     * @return ResponseEntity
     */
    public LoginResponse login(UserRequest request) {
        try {
            UsernamePasswordAuthenticationToken user =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
            Authentication auth = authenticationManager.authenticate(user);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (AuthenticationException | NullPointerException e) {
            System.out.println("Authentication error.\nExceptoin message: " + e.getMessage());
            return new LoginResponse(false);
        }
        return buildResponse(request.getEmail());
    }

    /**
     * Метод возвращает информацию о текущем авторизованном пользователе, если он авторизован.
     * Значение moderationCount содержит количество постов необходимых для проверки модераторами.
     * Считаются посты имеющие статус NEW и не проверерны модератором.
     * Если пользователь не модератор возращать 0 в moderationCount.
     *
     * @return ResponseEntity.
     */
    public ResponseEntity<LoginResponse> check() {
        ResponseEntity<LoginResponse> result = ResponseEntity.ok(new LoginResponse(false));
        try {
            SecurityUser userFromContext = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            result = ResponseEntity.ok(buildResponse(userFromContext.getUsername()));
        } catch (Exception e) {
            System.out.println("AuthService.check():\nНе удалось получить текущего юзера из контекста:\n" + e.getMessage());
        }
        return result;
    }

    /**
     * Метод регистрации нового юзера
     *
     * @param registerRequest - объект UserRequest из запроса
     * @return MainResponse.
     */
    public MainResponse register(UserRequest registerRequest) {

        ErrorsDto errorsDto = validateRequest(registerRequest);
        if (errorsDto != null)
            return new MainResponse(false, errorsDto);

        root.model.User user = new root.model.User(
                registerRequest.getName(),
                registerRequest.getEmail(),
                ENCODER.encode(registerRequest.getPassword()));

        userRepo.save(user);
        usersRolesRepo.save(new Role2User(user, new Role(1, "USER")));

        return new MainResponse(true, new ErrorsDto());
    }

    /**
     * Метод проверяет реквест-объект при регистрации нового юзера на наличие ошибок.
     *
     * @param registerRequest - реквест-объект.
     * @return ErrorsDto.
     */
    private ErrorsDto validateRequest(UserRequest registerRequest) {

        ErrorsDto errorsDto = null;
        if (!captchaService.validateCaptcha(registerRequest.getCaptcha(), registerRequest.getCaptchaSecret())) {
            errorsDto = new ErrorsDto();
            errorsDto.setCaptcha("Код с картинки введён неверно.");
        }
        if (userRepo.existsByEmail(registerRequest.getEmail())) {
            if (errorsDto == null)
                errorsDto = new ErrorsDto();
            errorsDto.setEmail("Этот e-mail уже зарегистрирован.");
        }
        if (registerRequest.getPassword().length() < 6) {
            if (errorsDto == null)
                errorsDto = new ErrorsDto();
            errorsDto.setPassword("Пароль короче 6-ти символов.");
        }
        if (registerRequest.getName().matches("\\W") || registerRequest.getName().length() < 3) {
            if (errorsDto == null)
                errorsDto = new ErrorsDto();
            errorsDto.setName("Имя должно состоять только из букв и быть длинной не менее 3 символов");
        }
        return errorsDto;
    }

    /**
     * Метод проверяет наличие в базе пользователя с указанным e-mail. Если пользователь найден, ему
     * должно отправляться письмо со ссылкой на восстановление пароля следующего вида -
     * /login/change-password/HASH, где HASH - сгенерированный код вида
     * b55ca6ea6cb103c6384cfa366b7ce0bdcac092be26bc0 (код должен генерироваться случайным образом
     * и сохраняться в базе данных в поле users.code).
     *
     * @param email email юзера.
     * @return MainResponse
     */
    public ResponseEntity<MainResponse> restore(String email) {
        if (!userRepo.existsByEmail(email))
            return new ResponseEntity<>(new MainResponse(false, "Пользователь с таким email не найден."), HttpStatus.BAD_REQUEST);
        else {
            String code = String.valueOf(email.hashCode());
            root.model.User user = userRepo.findByEmail(email);
            user.setCode(code);
            userRepo.save(user);
            emailService.sendRecoveryPasswordEmail(email, code);
            return ResponseEntity.ok(new MainResponse(true, new ErrorsDto()));
        }
    }

    /**
     * Метод заполняет поле moderationCount в классе UserDto.
     * В зависимости от того, является ли пользователь модератором.
     * Затем возвращает готовый LoginResponse.
     *
     * @param email - емэйл текущего юзера.
     * @return new LoginResponse
     */
    private LoginResponse buildResponse(String email) {

        root.model.User userFromDB = userRepo.findByEmail(email);

        UserDto userDto = new UserDto(userFromDB);
        int moderationCount = userFromDB.getIsModerator() == 0 ? 0 : postRepo.countByModerationStatus("NEW");
        userDto.setModerationCount(moderationCount);

        return new LoginResponse(true, userDto);
    }

    public MainResponse setNewPassword(NewPasswordRequest request) {
        ErrorsDto errorsDto = new ErrorsDto();
        boolean result = true;

        root.model.User user = userRepo.findByCode(request.getCode());
        if (user == null) {
            errorsDto.setCode("Ссылка для восстановления пароля устарела." +
                    "<a href=\\\"/auth/restore\\\">Запросить ссылку снова</a>");
            result = false;
        }

        CaptchaCode captcha = captchaCodeRepo.findBySecretCode(request.getCaptchaSecret());
        if (!captcha.getCode().equals(request.getCaptcha())) {
            errorsDto.setCaptcha("Код с картинки введён неверно");
            result = false;
        }

        if (request.getPassword().length() < 6) {
            errorsDto.setPassword("Пароль короче шести символов");
            result = false;
        }

        if (result){
            user.setPassword(ENCODER.encode(request.getPassword()));
            userRepo.save(user);
            return new MainResponse(true);
        }

        else return new MainResponse(false, errorsDto);
    }
}
/*
        Заметки для себя:

        AuthenticationManager получается в SecurityConfig.
       AuthenticationManager в методе authenticate возвращает одно из трёх:
       1. объект Authentication, если он может проверить юзера
       2. AuthenticationException, если юзер недопустим.
       3. null, если не может определить.

       Из входящих данных создаётся объект для последующей его сверки
        UsernamePasswordAuthenticationToken userFromRequestBody =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        метод authenticate проверяет юзера и возвращает объект Authenticate
        Authentication auth = authenticationManager.authenticate(userFromRequestBody);

        объект Authenticate помещается в SecurityContextHolder - хранилище аутентифицированных юзеров
        SecurityContextHolder.getContext().setAuthentication(auth);

        получить юзера (UserDetails) обратно:
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        На основе объекта Authenticate создаётся объект User(org.springframework.security.core.userdetails.User), имплементирующийся от UserDetails
        User userUD = (User) auth.getPrincipal();

        У этого юзера берётся username (здесь это e-mail, который был передан в RequestBody)
        return ResponseEntity.ok(getLoginResponse(userUD.getUsername()));
  */
