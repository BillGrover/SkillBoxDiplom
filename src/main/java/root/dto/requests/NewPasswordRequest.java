package root.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewPasswordRequest {

    @JsonProperty("password")
    private String password;

    @JsonProperty("code")
    private String code;

    @JsonProperty("captcha")
    private String captcha;

    @JsonProperty("captcha_secret")
    private String captchaSecret;

    public NewPasswordRequest() {
    }

    public NewPasswordRequest(String password, String code, String captcha, String captchaSecret) {
        this.password = password;
        this.code = code;
        this.captcha = captcha;
        this.captchaSecret = captchaSecret;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
}
