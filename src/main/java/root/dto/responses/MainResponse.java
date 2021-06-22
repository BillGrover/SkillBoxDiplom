package root.dto.responses;

import root.dto.ErrorsDto;

public class MainResponse {
    private boolean result;
    private ErrorsDto errors;

    public MainResponse(boolean result, ErrorsDto errors) {
        this.result = result;
        this.errors = errors;
    }

    public MainResponse(boolean result, String error) {
        this(result, new ErrorsDto(error));
    }

    public MainResponse(boolean result) {
        this.result = result;
    }

    public MainResponse() {
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ErrorsDto getErrors() {
        return errors;
    }

    public void setErrors(ErrorsDto errors) {
        this.errors = errors;
    }
}
