package root.dto.responses;

import org.json.simple.JSONObject;
import root.dto.ErrorsDto;

import java.util.List;

public class CalendarResponse extends MainResponse {
    List<Integer> years;
    JSONObject posts;

    public CalendarResponse(boolean result, ErrorsDto errors) {
        super(result, errors);
    }

    public CalendarResponse(boolean result, List<Integer> years, JSONObject posts) {
        super(result);
        this.years = years;
        this.posts = posts;
    }

    public List<Integer> getYears() {
        return years;
    }

    public JSONObject getPosts() {
        return posts;
    }
}
