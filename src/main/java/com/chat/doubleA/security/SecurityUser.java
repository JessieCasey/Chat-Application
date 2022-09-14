package com.chat.doubleA.security;

import com.chat.doubleA.entities.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
public class SecurityUser implements UserDetails {
    private String id;
    private String username;
    private String password;
    private boolean isActive;
    private Collection<? extends GrantedAuthority> roles;

    public SecurityUser(String id,
                        String username,
                        String password,
                        boolean isActive,
                        Collection<? extends GrantedAuthority> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isActive = isActive;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    public String getId() {
        return id;
    }

    public static UserDetails fromUser(User user) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole().getName()));

        return new SecurityUser(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                true,
                grantedAuthorities
        );
    }
}