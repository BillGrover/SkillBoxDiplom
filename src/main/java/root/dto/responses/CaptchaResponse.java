package root.dto.responses;

public class CaptchaResponse extends MainResponse{
    private String secret;
    private String image;

    public CaptchaResponse(boolean result, String secret, String image) {
        super(result);
        this.secret = secret;
        this.image = "data:image/png;base64, " + image;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
