package root.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentRequest {
    @JsonProperty("parent_id")
    private String parentId;

    @JsonProperty("post_id")
    private int postId;

    @JsonProperty("text")
    private String text;

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getParentId() {
        return parentId;
    }

    public int getPostId() {
        return postId;
    }

    public String getText() {
        return text;
    }
}
