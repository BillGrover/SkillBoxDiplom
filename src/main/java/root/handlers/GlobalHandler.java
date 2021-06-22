package root.handlers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import root.model.User;
import root.repositories.UserRepository;
import root.security.SecurityUser;

@Service
public class GlobalHandler {
private final UserRepository userRepo;

    public GlobalHandler(UserRepository userRepo) {
        this.userRepo = userRepo;
    }


    /**
     * Метод получает текущего юзера из контекста Spring Security.
     *
     * @return
     */
    public User getUserFromContext() {
        User user = null;
        try {
            SecurityUser userFromContext = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            user = userRepo.findByEmail(userFromContext.getUsername());
        } catch (Exception e) {
            System.out.println("INFO: Неудачная попытка получить текущего юзера из контекста.");
        }
        return user;
    }
}
