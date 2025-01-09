package com.redhat.widget.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@EnableConfigurationProperties(ApplicationUsers.class)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(new AntPathRequestMatcher("/login.faces"))
                        .permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/jakarta.faces.resource/**"))
                        .permitAll()
                        .anyRequest()
                        .hasAnyRole("ADMIN", "USER"))
                .formLogin((formLogin) -> formLogin.loginPage("/login.faces")
                        .permitAll()
                        .failureUrl("/login.faces?error=true")
                        .defaultSuccessUrl("/widgets.faces"))
                .logout((logout) -> logout.logoutSuccessUrl("/login.faces").deleteCookies("JSESSIONID"));
        return http.build();
    }

    @Scope("prototype")
    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {

        return new MvcRequestMatcher.Builder(introspector);
    }

    /**
     * UserDetailsService that configures an in-memory users store.
     *
     * @param applicationUsers - autowired users from the application.yml file
     * @return a manager that keeps all the users' info in the memory
     */
    @Bean
    public InMemoryUserDetailsManager userDetailsService(ApplicationUsers applicationUsers) {

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        InMemoryUserDetailsManager result = new InMemoryUserDetailsManager();
        for (UserCredentials userCredentials : applicationUsers.getUsersCredentials()) {
            result.createUser(User.builder()
                    .username(userCredentials.getUsername())
                    .password(encoder.encode(userCredentials.getPassword()))
                    .authorities(userCredentials.getAuthorities().toArray(new String[0]))
                    .build());
        }
        return result;
    }

}
