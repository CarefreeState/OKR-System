package com.macaku.user.domain.dto.detail;

import com.alibaba.fastjson.annotation.JSONField;
import com.macaku.user.domain.po.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser implements UserDetails {

    private User user;

    private List<String> permissions;

    @JSONField(serialize = false)//代表，由不通过fastjson序列化，用系统redis其他的方式存入redis
    private List<GrantedAuthority> authorities;//com.alibaba.fastjson.JSONException: autoType is not support.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(Objects.isNull(this.authorities)) {
            // 将 permissions 的权限信息封装成 SimpleGrantedAuthority 象，并且也是集合
            this.authorities = this.permissions.stream()
                    .parallel()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.getNickname();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public LoginUser(User user, List<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }
}
