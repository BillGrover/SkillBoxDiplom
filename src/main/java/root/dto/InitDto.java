package root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InitDto {

    private String title;
    private String subtitle;
    private String phone;
    private String email;
    private String copyright;
    @JsonProperty("copyrightFrom")
    private String copyrightFrom;

    public InitDto(String title, String subtitle, String phone, String email, String copyright, String copyrightFrom) {
        this.title = title;
        this.subtitle = subtitle;
        this.phone = phone;
        this.email = email;
        this.copyright = copyright;
        this.copyrightFrom = copyrightFrom;
    }

    public InitDto() {
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getCopyrightFrom() {
        return copyrightFrom;
    }
}
