package com.group4.chatapp.configs;

import org.msgpack.jackson.dataformat.MessagePackMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class MsgpackConfig implements WebMvcConfigurer {

    @Bean
    public HttpMessageConverter<?> msgpackMessageConverter() {

        var objectMapper = new MessagePackMapper();
        objectMapper.handleBigIntegerAndBigDecimalAsString();

        var messageConverter = new AbstractJackson2HttpMessageConverter(objectMapper) {};

        var supportedMediaTypes = List.of(
            new MediaType("application", "msgpack"),
            new MediaType("application", "x-msgpack")
        );
        messageConverter.setSupportedMediaTypes(supportedMediaTypes);

        return messageConverter;
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }
}
