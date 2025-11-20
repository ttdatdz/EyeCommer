package com.eyecommer.Backend.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;

@Configuration
public class ThymeleafConfig {
    //Gắn TemplateResolver(bộ tìm kiếm template) vào SpringTemplateEngine. Để SpringTemplateEngine lấy và truyền data vào rồi render ra html
    @Bean
    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        springTemplateEngine.addTemplateResolver(emailTemplateResolver());
        return springTemplateEngine;
    }
    //Cấu hình TemplateResolver(bộ tìm kiếm template) để nó biết tìm Template từ đâu
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver(); //tao TemplateResolver
        emailTemplateResolver.setPrefix("/templates/"); //xác định thư mục chứa template
        emailTemplateResolver.setSuffix(".html"); //xác định đuôi file template
        emailTemplateResolver.setTemplateMode(TemplateMode.HTML); //Chỉ định engine parse nội dung theo cách nào
        emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name()); //Giúp SpringtemplateEngine khi render ra html tránh lỗi tiếng việt
        emailTemplateResolver.setCacheable(false); // đặt cache bằng fasle, để mỗi lần request sẽ đọc lại file templae
        return emailTemplateResolver;
    }
}
