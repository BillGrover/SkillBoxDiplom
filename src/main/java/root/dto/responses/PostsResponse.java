package root.dto.responses;

import org.springframework.data.domain.Page;
import root.dto.ErrorsDto;
import root.dto.PostDto;
import root.model.Post;

import java.util.List;
import java.util.stream.Collectors;

public class PostsResponse extends MainResponse{
    private long count;
    private List<PostDto> posts;

    public PostsResponse(Page<Post> posts) {
        this.count = posts.getTotalElements();
        this.posts = posts
                .stream()
                .map(PostDto::new)
                .collect(Collectors.toList());
    }

    public PostsResponse() {
    }

    public PostsResponse(boolean result, ErrorsDto errors) {
        super(result, errors);
    }

    public long getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PostDto> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDto> posts) {
        this.posts = posts;
    }
}
