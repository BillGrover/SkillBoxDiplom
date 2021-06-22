package root.dto.responses;

import root.dto.TagDto;

import java.util.List;

public class TagResponse extends MainResponse{

    private List<TagDto> tags;

    public TagResponse(List<TagDto> tags) {
        this.tags = tags;
    }

    public List<TagDto> getTags() {
        return tags;
    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }
}
