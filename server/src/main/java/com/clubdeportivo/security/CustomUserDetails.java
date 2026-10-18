package com.clubdeportivo.security;

import com.clubdeportivo.domain.enums.UserStatus;
import com.clubdeportivo.domain.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    @Getter
    private final UUID id;
    private final String email;
    private final String password;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;
    @Getter
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPasswordHash();
        this.enabled = user.getStatus() == UserStatus.ACTIVE;

        if (user.getRoles() != null) {
            this.authorities = user.getRoles().stream()
                    .map(role -> {
                        String roleName = role.getName();
                        if (roleName != null && !roleName.startsWith("ROLE_")) {
                            roleName = "ROLE_" + roleName;
                        }
                        return new SimpleGrantedAuthority(roleName);
                    })
                    .collect(Collectors.toSet());
        } else {
            this.authorities = Collections.emptySet();
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user != null && user.getStatus() != UserStatus.CANCELLED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

