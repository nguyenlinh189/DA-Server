package com.example.security;


import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.exception.ResourceNotFoundException;
import com.example.model.User;
import com.example.repository.UserRepository;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepo;
	
	// cho cho phep nguoi dung dang nhap khi xac thuc tai khoan
	@Override
	public UserDetails loadUserByUsername(String email) {
		User users = userRepo.findByEmail(email);
        if(users==null)
           throw new UsernameNotFoundException("User not found with email : " + email);
        return UserPrincipal.create(users);
	}
	@Transactional
    public UserDetails loadUserById(int id) {
        User user = userRepo.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("User", "id", id)
        );

        return UserPrincipal.create(user);
    }
}
