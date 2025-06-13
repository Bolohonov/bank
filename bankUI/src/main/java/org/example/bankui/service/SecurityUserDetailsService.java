package org.example.bankui.service;

import org.example.bankui.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountService accountService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserResponse response = accountService.getUserInfo(username);
        if (!response.getStatusCode().equals("0")) {
            throw new UsernameNotFoundException(response.getStatusMessage());
        }
        return response;
    }

}
