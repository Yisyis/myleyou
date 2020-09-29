package com.leyou.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class LeyouCorsConfiguration {

    @Bean
    public CorsFilter corsFilter() {
        //1. 添加CORS配置信息
        CorsConfiguration configuration = new CorsConfiguration();
        // 1.1 允许的域， 不要写 *， 否则 Cookie无法使用
        configuration.addAllowedOrigin("http://manage.leyou.com");
        // 1.2 是否发送Cookie信息
        configuration.setAllowCredentials(true);
        // 1.3 允许的请求方式
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("HEAD");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("PATCH");
        // 1.4 允许的头信息
        configuration.addAllowedHeader("*");

        //2. 添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**", configuration);

        //3. 返回新的CorsFilter
        return new CorsFilter(configurationSource);

    }


}
