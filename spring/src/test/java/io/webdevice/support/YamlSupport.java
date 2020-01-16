package io.webdevice.support;

import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YamlSupport {

    @Bean
    public ApplicationConversionService conversionService() {
        return new ApplicationConversionService();
    }
}
