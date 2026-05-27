package com.unistay.housing_management_system.services;

import com.unistay.housing_management_system.entity.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null)
        {
            System.out.println("No authenticated user found.  11 ");
            return null;
        }
        if(!authentication.isAuthenticated())
        {
             System.out.println("No authenticated user found.  22 ");
             return null;
        }
        if(authentication instanceof AnonymousAuthenticationToken) {
            System.out.println("No authenticated user found. 33 ");
            return null;
        }

        /*if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            System.out.println("No authenticated user found.");
            return null;
        }*/

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user.getId();
        }

        // In case another UserDetails implementation is used
        if (principal instanceof UserDetails) {
            // Note: In this project, username = email. If you need the id, prefer using the entity User as principal.
            // Returning null here keeps behavior consistent with unauthenticated access.
            System.out.println("Authenticated principal is UserDetails but not our User entity. Returning null for user ID.");
            return null;
        }

        System.out.println("Authenticated principal is of unexpected type: " + principal.getClass().getName());
        return null;
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
