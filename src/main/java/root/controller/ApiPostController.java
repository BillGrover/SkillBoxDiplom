package root.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import root.dto.requests.PostRequest;
import root.dto.responses.OnePostResponse;
import root.dto.responses.PostsResponse;
import root.services.PostService;

@Controller
@RequestMapping("/api/post")
public class ApiPostController {

    private PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<PostsResponse> getAllPosts(
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit,
            @RequestParam String mode) {
        return postService.getPosts(offset, limit, mode);
    }

    @GetMapping("/search")
    public ResponseEntity<PostsResponse> getPostsByQuery(
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit,
            @RequestParam String query) {
        return postService.getByQuery(offset, limit, query);
    }

    @GetMapping("/byDate")
    @ResponseBody
    public ResponseEntity<PostsResponse> getPostsByDate(
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit,
            @RequestParam(defaultValue = "1970-01-01") String date) {
        return postService.getByDate(offset, limit, date);
    }

    @GetMapping("/byTag")
    @ResponseBody
    public ResponseEntity<PostsResponse> byTag(
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit,
            @RequestParam String tag) {
        return postService.getByTag(offset, limit, tag);
    }

    @GetMapping("/moderation")
    @ResponseBody
    public ResponseEntity<PostsResponse> moderation(
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit,
            @RequestParam String status) {
        return postService.getModeration(offset, limit, status);
    }

    @GetMapping("/my")
    @ResponseBody
    public ResponseEntity<PostsResponse> my(
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit,
            @RequestParam String status) {
        return postService.getMy(offset, limit, status);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<OnePostResponse> postId(
            @PathVariable int id) {
        return postService.getOnePost(id);
    }

    @PostMapping
    public ResponseEntity<PostsResponse> postPost(
            @RequestBody PostRequest postRequest) {
        return postService.savePost(postRequest);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<PostsResponse> updatePost(
            @PathVariable int id,
            @RequestBody PostRequest postRequest) {
        return postService.updatePost(id, postRequest);
    }
}
