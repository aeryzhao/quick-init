package top.amfun.quickinit.common;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import top.amfun.quickinit.entity.Permission;
import top.amfun.quickinit.entity.Role;
import top.amfun.quickinit.entity.User;

import java.util.Collection;
import java.util.List;

/**
 * SpringSecurity需要的用户详情
 */
@Data
public class UserDetailsImpl implements UserDetails {
    private User user;
    private List<Role> roles;
    private List<Permission> permissions;

    public UserDetailsImpl(User user) {
        this.user = user;
    }
    public UserDetailsImpl(User user, List<Role> roles, List<Permission> permissions) {
        this.user = user;
        this.roles = roles;
        this.permissions = permissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(permissions.stream().map(Permission::getCode).toArray(String[]::new));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
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
}
