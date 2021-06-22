package root;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {
    @Value("${upload.path}")
    private String imagesPath;
    @Value("${avatar.path}")
    private String avatarsPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/**")
                .addResourceLocations("file:src/main/resources/static/");
        registry
                .addResourceHandler("/" + imagesPath + "/**")
                .addResourceLocations("file:" + imagesPath + "/");
        registry
                .addResourceHandler("/post/" + imagesPath + "/**")
                .addResourceLocations("file:" + imagesPath + "/");
        registry
                .addResourceHandler("/" + avatarsPath + "/**")
                .addResourceLocations("file:" + avatarsPath + "/");
    }
}

//Еще примеры:
//        registry
//                .addResourceHandler("/js/**")
//                .addResourceLocations("file:src/main/resources/static/js/");
//        registry
//                .addResourceHandler("/css/**")
//                .addResourceLocations("file:src/main/resources/static/css/");
//        registry
//                .addResourceHandler("/fonts/**")
//                .addResourceLocations("file:src/main/resources/static/fonts/");
//        registry
//                .addResourceHandler("/img/**")
//                .addResourceLocations("file:src/main/resources/static/img/");