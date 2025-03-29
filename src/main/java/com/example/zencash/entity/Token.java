package com.example.zencash.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Table(name = "tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = false, unique = true)
    String token;

    @Column(nullable = false, unique = true)
    String refreshToken;

    @Column(nullable = false)
    boolean revoked;

    @Column(nullable = false)
    boolean expired;

    @Column(nullable = false)
    Date expirationDate;

    @Column(nullable = false)
    Date refreshExpirationDate;
}
