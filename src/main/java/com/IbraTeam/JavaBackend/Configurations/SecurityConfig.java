package com.IbraTeam.JavaBackend.Configurations;

import com.IbraTeam.JavaBackend.Models.Response;
import com.IbraTeam.JavaBackend.Services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final UserService userService;
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests ->
                        requests
                                .requestMatchers(HttpMethod.POST,"/api/account/logout").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/account/profile").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/account/role").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/account/users").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/api/account/role*").hasAnyRole("DEAN", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/account/role/*").hasAnyRole("DEAN", "ADMIN")
                                .requestMatchers(HttpMethod.PATCH, "/api/account/dean*").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, " /api/audience-key").hasRole("DEAN")
                                .requestMatchers(HttpMethod.DELETE, " /api/audience-key").hasRole("DEAN")
                                .requestMatchers(HttpMethod.PATCH, "/api/audience-key/give/*").hasAnyRole("STUDENT", "TEACHER", "DEAN")
                                .requestMatchers(HttpMethod.POST, "/api/audience-key/get").hasAnyRole("STUDENT", "TEACHER", "DEAN")
                                .requestMatchers(HttpMethod.POST, "/api/audience-key/return/*").hasAnyRole("STUDENT", "TEACHER", "DEAN")
                                .anyRequest().permitAll())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            Response responseData = new Response(HttpStatus.FORBIDDEN.value(), "У пользователя нет прав доступа");
                            String jsonResponse = new ObjectMapper().writeValueAsString(responseData);
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json;charset=UTF-8");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(jsonResponse);
                        }
                ))
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

       return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userService);

        return daoAuthenticationProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
}
