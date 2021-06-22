package root.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import root.dto.ErrorsDto;
import root.dto.requests.NewPasswordRequest;
import root.dto.requests.UserRequest;
import root.dto.responses.CaptchaResponse;
import root.dto.responses.LoginResponse;
import root.dto.responses.MainResponse;
import root.services.AuthService;
import root.services.CaptchaService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthService authService;
    private final CaptchaService captchaService;

    @Autowired
    public ApiAuthController(AuthService authService, CaptchaService captchaService) {
        this.authService = authService;
        this.captchaService = captchaService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody UserRequest userRequest) {
        return authService.login(userRequest);
    }

    @GetMapping("/check")
    public ResponseEntity<LoginResponse> check() {
        return authService.check();
    }

    @GetMapping("/logout")
    public ResponseEntity<MainResponse> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MainResponse(true, new ErrorsDto()));
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> captcha() {
        return captchaService.createCaptcha();
    }

    @PostMapping("/register")
    public MainResponse register(@RequestBody UserRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/restore")
    public ResponseEntity<MainResponse> restore(@RequestBody Map<String, String> map) {
        return authService.restore(map.get("email"));
    }

    @PostMapping("/password")
    public MainResponse newPassword(@RequestBody NewPasswordRequest request) {
        return authService.setNewPassword(request);
    }
}

