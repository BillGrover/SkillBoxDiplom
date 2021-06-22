package root.dto.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;

public class UserRequest {

    @JsonAlias({"e_mail"})
    private String email;
    private String password;
    private String name;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;
    private MultipartFile photo;
    @JsonProperty("removePhoto")
    private int removePhoto;

    public UserRequest() {
    }

    public UserRequest(String password, String name, String removePhoto, MultipartFile photo, String email) {
        this.password = password;
        this.name = name;
        this.removePhoto = removePhoto.equals("1") ? 1 : 0;
        this.email = email;
        this.photo = photo;
    }

    public UserRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserRequest(String email, String password, String name, String captcha, String captchaSecret) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.captcha = captcha;
        this.captchaSecret = captchaSecret;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCaptchaSecret() {
        return captchaSecret;
    }

    public void setCaptchaSecret(String captchaSecret) {
        this.captchaSecret = captchaSecret;
    }

    public MultipartFile getPhoto() {
        return photo;
    }

    public void setPhoto(MultipartFile photo) {
        this.photo = photo;
    }

    public boolean isRemovePhoto() {
        return removePhoto == 1;
    }

    public void setRemovePhoto(boolean removePhoto) {
        this.removePhoto = removePhoto ? 1 : 0;
    }
}
