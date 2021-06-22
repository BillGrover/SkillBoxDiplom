package root.services;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import root.dto.CalendarDto;
import root.dto.ErrorsDto;
import root.dto.responses.CalendarResponse;
import root.handlers.PostHandler;
import root.repositories.PostRepository;

import java.util.Calendar;
import java.util.List;

@Service
public class CalendarService {
    private final PostRepository postRepo;
    private final PostHandler handler;

    public CalendarService(PostRepository postRepo, PostHandler handler) {
        this.postRepo = postRepo;
        this.handler = handler;
    }

    public ResponseEntity<CalendarResponse> getCalendar(String yearRequest) {
        int year;
        int parseResult = handler.parseInt(yearRequest);
        if (parseResult == 0)
            year = Calendar.getInstance().get(Calendar.YEAR);
        else if (parseResult > 0)
            year = parseResult;
        else
            return new ResponseEntity<>(new CalendarResponse(false, new ErrorsDto(
                    "Неверно указан год: параметр year = " + yearRequest)), HttpStatus.BAD_REQUEST);

        List<Integer> yearsWithPosts = postRepo.findYearsWithPosts();
        List<CalendarDto> count2Date = postRepo.findCount2Date(year);
        JSONObject jsonObject = new JSONObject();
        count2Date.forEach(dto -> jsonObject.put(dto.getTime(), dto.getCount()));
        CalendarResponse response = new CalendarResponse(true, yearsWithPosts, jsonObject);
        return ResponseEntity.ok(response);
    }
}
