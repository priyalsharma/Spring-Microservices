package com.psprojects.ProductService.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true) part of way 1
public class WebSecurityConfig {
    //override security filter chain configuration: way 1 --> same as OrderService-> security -> WebSecurityConfig
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests(
//                        authorizeRequest -> authorizeRequest
//                                .anyRequest()
//                                .authenticated())
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
//        return http.build();
        //way 2-----> this way has been removed in upgraded version of spring boot and a uniform way has been suggested, its always way 1 now
        http
                .authorizeHttpRequests(
                        authorizeRequest -> authorizeRequest
                                .requestMatchers("/payment/**") //allows all urls that starts with /payment and u can individually remove auth at url levels
                                .hasAuthority("SCOPE_internal")
                                .anyRequest()
                                .authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
