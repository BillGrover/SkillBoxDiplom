package root.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import root.dto.GlobalSettingsDto;
import root.dto.InitDto;
import root.dto.requests.UserRequest;
import root.dto.responses.CalendarResponse;
import root.dto.responses.MainResponse;
import root.dto.responses.StatisticsResponse;
import root.dto.responses.TagResponse;
import root.services.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {
    @Value("${blog.title}")
    private String title;
    @Value("${blog.subtitle}")
    private String subtitle;
    @Value("${blog.phone}")
    private String phone;
    @Value("${blog.emailInit}")
    private String email;
    @Value("${blog.copyright}")
    private String copyright;
    @Value("${blog.copyrightFrom}")
    @JsonProperty("copyrightFrom")
    private String copyrightFrom;

    private final GlobalSettingsService globalSettingsService;
    private final TagService tagService;
    private final CalendarService calendarService;
    private final UserService userService;
    private final StatisticsService statisticsService;

    public ApiGeneralController(GlobalSettingsService globalSettingsService, TagService tagService, CalendarService calendarService, UserService userService, StatisticsService statisticsService) {
        this.globalSettingsService = globalSettingsService;
        this.tagService = tagService;
        this.calendarService = calendarService;
        this.userService = userService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/init")
    public ResponseEntity<InitDto> init() {
        InitDto response = new InitDto(title, subtitle, phone, email, copyright, copyrightFrom);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/settings")
    public ResponseEntity<GlobalSettingsDto> getSettings() {
        return globalSettingsService.getSettings();
    }

    @PutMapping("/settings")
    public void putSettings(@RequestBody GlobalSettingsDto request) {
        globalSettingsService.putSettings(request);
    }

    @GetMapping("/tag")
    public ResponseEntity<TagResponse> tags(@RequestParam(required = false) String query) {
        return tagService.getTags(query);
    }

    @GetMapping("/calendar")
    public ResponseEntity<CalendarResponse> calendar(@RequestParam(required = false) String year) {
        return calendarService.getCalendar(year);
    }

    @PostMapping(value = "/profile/my", consumes = {"multipart/form-data"})
    public MainResponse profileMy(
            @RequestParam(name = "photo", required = false) MultipartFile photo,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "password", required = false) String password,
            @RequestParam(name = "removePhoto", required = false, defaultValue = "0") String removePhoto) {
        return userService.editProfile(new UserRequest(password, name, removePhoto, photo, email));
    }

    @PostMapping(value = "/profile/my")
    public MainResponse profileMy(@RequestBody Map<String, String> requestMap) {
        UserRequest request = userService.mapToUserRequest(requestMap);
        return userService.editProfile(request);
    }

    @GetMapping("/statistics/my")
    public StatisticsResponse statisticsMy() {
        return statisticsService.getMyStatistic();
    }

    @GetMapping("/statistics/all")
    public StatisticsResponse statisticsAll() {
        return statisticsService.getAllStatistic();
    }
}
