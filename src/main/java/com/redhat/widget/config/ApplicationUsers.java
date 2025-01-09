package com.redhat.widget.config;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application-users")
@Getter
@Setter
public class ApplicationUsers {

    private List<UserCredentials> usersCredentials;

}