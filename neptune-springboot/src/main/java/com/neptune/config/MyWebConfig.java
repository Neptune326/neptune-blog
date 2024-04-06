package com.neptune.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebConfig implements WebMvcConfigurer {

   /* @Autowired
    private GlobalProperties globalProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/attachment/**")
                .addResourceLocations("file:" + globalProperties.getUploadPath());
    }*/

}
