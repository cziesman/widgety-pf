package com.redhat.widget.config;

import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@EnableConfigurationProperties(ApplicationUsers.class)
@Slf4j
public class SecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {

        http
                .csrf(Customizer.withDefaults());
        http
                .cors(Customizer.withDefaults());
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(new AntPathRequestMatcher("/favicon.ico"))
                        .permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/actuator/**"))
                        .permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/login/**"))
                        .permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/logout/**"))
                        .permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/oauth2/**"))
                        .permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/resources/**"))
                        .permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/jakarta.faces.resource/**"))
                        .permitAll()
                        .anyRequest()
                        .authenticated());
        http
//                .oauth2Login(withDefaults());
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userAuthoritiesMapper(userAuthoritiesMapper())));
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        //        http
//                .formLogin((formLogin) -> formLogin.loginPage("/login.faces")
//                        .failureUrl("/login.faces?error=true")
//                        .defaultSuccessUrl("/widgets.faces")
//                        .permitAll());
//        http
//                .logout((logout) -> logout
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                        .clearAuthentication(true)
//                        .invalidateHttpSession(true)
//                        .logoutSuccessUrl("/widgets.faces")
//                        .deleteCookies("JSESSIONID"));

        http
                .oidcLogout((logout) -> logout
                        .backChannel(Customizer.withDefaults()));

        return http.build();
    }

    @Scope("prototype")
    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {

        return new MvcRequestMatcher.Builder(introspector);
    }


    private GrantedAuthoritiesMapper userAuthoritiesMapper() {

        // this is a complete hack because I cannot figure out how to get keycloak to return the custom client roles.
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority oidcUserAuthority) {

                    OidcIdToken idToken = oidcUserAuthority.getIdToken();

                    if ("jane".equals(idToken.getPreferredUsername())) {
                        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
                        mappedAuthorities.add(simpleGrantedAuthority);
                    }
                    SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
                    mappedAuthorities.add(simpleGrantedAuthority);
                }
            });

            return mappedAuthorities;
        };
    }

//    /**
//     * UserDetailsService that configures an in-memory users store.
//     *
//     * @param applicationUsers - autowired users from the application.yml file
//     * @return a manager that keeps all the users' info in the memory
//     */
//    @Bean
//    public InMemoryUserDetailsManager userDetailsService(ApplicationUsers applicationUsers) {
//
//        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        InMemoryUserDetailsManager result = new InMemoryUserDetailsManager();
//        for (UserCredentials userCredentials : applicationUsers.getUsersCredentials()) {
//            result.createUser(User.builder()
//                    .username(userCredentials.getUsername())
//                    .password(encoder.encode(userCredentials.getPassword()))
//                    .authorities(userCredentials.getAuthorities().toArray(new String[0]))
//                    .build());
//        }
//        return result;
//    }
}
