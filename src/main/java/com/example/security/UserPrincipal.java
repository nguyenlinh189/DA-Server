package com.example.security;

import com.example.model.EProvider;
import com.example.model.ERole;
import com.example.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserPrincipal implements OAuth2User, UserDetails {
    private int id;
    private String name;
    private String email;
    private String password;
    private String address;
    private String phone;
    private Date createAt;
    private Date updateAt;
    private EProvider provider;
    private ERole role;
    private boolean enabled;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    

    public UserPrincipal(int id, String name, String email, String password, String address, String phone,
			Date createAt, Date updateAt, EProvider provider, ERole role,boolean enabled,
			Collection<? extends GrantedAuthority> authorities) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.address = address;
		this.phone = phone;
		this.createAt = createAt;
		this.updateAt = updateAt;
		this.provider = provider;
		this.role = role;
		this.enabled=enabled;
		this.authorities = authorities;
	}

	public static UserPrincipal create(User user) {
    	List<GrantedAuthority> authorities = Collections.
                singletonList(new SimpleGrantedAuthority(user.getRole().toString()));
		return new UserPrincipal(user.getId(), user.getName(),user.getEmail() ,user.getPassword(), 
				user.getAddress(), user.getPhone(),user.getCreateAt(), user.getUpdateAt(), user.getProvider(),user.getRole(),user.isEnabled(), authorities);
    }

    public static UserPrincipal create(User users, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(users);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
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
        return enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return name;
    }
    public String getAddress() {
    	return address;
    }

	public String getPhone() {
		return phone;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public Date getUpdateAt() {
		return updateAt;
	}

	public EProvider getProvider() {
		return provider;
	}

	public ERole getRole() {
		return role;
	}

}
