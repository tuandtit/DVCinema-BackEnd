package com.cinema.booking_app.user.entity;

import com.cinema.booking_app.common.base.entity.AbstractAuditingEntity;
import com.cinema.booking_app.common.enums.AccountStatus;
import com.cinema.booking_app.common.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Builder
@Table(
        name = "accounts",
        indexes = {
                @Index(
                        columnList = "username",
                        unique = true
                )
        }
)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountEntity extends AbstractAuditingEntity<Long> {

    @Column(name = "username", unique = true, nullable = false, updatable = false)
    String username;

    @Column(name = "password_hash", nullable = false)
    String passwordHash;

    @Column(name = "avatar", columnDefinition = "text")
    String avatar;

    @Column(name = "display_name", nullable = false)
    String displayName;

    @Column(unique = true)
    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatus status;

    @Column(name = "auth_provider")
    @Enumerated(EnumType.STRING)
    AuthProvider authProvider = AuthProvider.EMAIL_AND_PASSWORD;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_roles",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    List<RoleEntity> roles = new ArrayList<>();

    public void addRole(RoleEntity role) {
        if (ObjectUtils.isEmpty(roles)) {
            this.roles = new ArrayList<>();
        }
        roles.add(role);
    }
}