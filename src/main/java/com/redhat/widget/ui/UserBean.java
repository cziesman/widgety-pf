package com.redhat.widget.ui;

import jakarta.enterprise.context.RequestScoped;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

@Component
@RequestScoped
@Slf4j
public class UserBean {

    public String getLoggedInUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            LOG.debug("{}", principal);
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else if (principal instanceof DefaultOAuth2User) {

                return ((DefaultOAuth2User) principal).getAttribute("name");
            } else {
                return principal.toString(); // For cases like OAuth where principal may be a string
            }
        }
        return null; // No authenticated user
    }

}
