package com.redhat.widget.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

@Configuration
public class UserAuthoritiesMapper {

    @Bean
    public GrantedAuthoritiesMapper mapper() {

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
}
