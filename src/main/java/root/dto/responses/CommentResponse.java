package root.dto.responses;

import root.dto.ErrorsDto;

public class CommentResponse extends MainResponse{
    private int id;

    public CommentResponse(int id) {
        this.id = id;
    }

    public CommentResponse(boolean result, ErrorsDto errors) {
        super(result, errors);
    }

    public int getId() {
        return id;
    }
}
