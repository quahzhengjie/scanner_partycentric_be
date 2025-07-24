package com.bkb.scanner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "csob_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"id"})
@ToString(callSuper = true, exclude = {"password"})
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(length = 100)
    private String department;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private boolean enabled = true;

    private LocalDateTime lastLogin;

    @Column(length = 255)
    private String profilePicture;

    // Spring Security UserDetails methods
    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    @Transient
    public String getUsername() {
        return email;
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isEnabled() {
        return enabled && isActive;
    }

    // Enum for user roles
    public enum UserRole {
        RM("Relationship Manager"),
        CHECKER("Checker"),
        COMPLIANCE("Compliance Officer"),
        GM("General Manager"),
        ADMIN("Administrator");

        private final String displayName;

        UserRole(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Helper methods
    public boolean hasRole(UserRole... roles) {
        for (UserRole r : roles) {
            if (this.role == r) {
                return true;
            }
        }
        return false;
    }

    public boolean canApprove() {
        return hasRole(UserRole.CHECKER, UserRole.COMPLIANCE, UserRole.GM);
    }

    public boolean canCreateCase() {
        return hasRole(UserRole.RM, UserRole.ADMIN);
    }

    public boolean canManageUsers() {
        return hasRole(UserRole.ADMIN);
    }
}