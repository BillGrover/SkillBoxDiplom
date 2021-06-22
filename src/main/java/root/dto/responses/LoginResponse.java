package root.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import root.dto.UserDto;

public class LoginResponse extends MainResponse{
    @JsonProperty("user")
    private UserDto userDto;

    public LoginResponse(boolean result) {
        super(result);
    }

    public LoginResponse(boolean result, UserDto userDto) {
        super(result);
        this.userDto = userDto;
    }

    public UserDto getUserDto() {
        return userDto;
    }
}
