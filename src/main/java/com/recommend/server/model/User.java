package com.recommend.server.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.recommend.server.dto.Coordinates;
import com.recommend.server.dto.UserDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    String name;
    @Column(unique = true)
    String email;
    String password;
    Coordinates locale;

    @OneToOne
    @JoinColumn(name = "capelinho_id")
    @JsonManagedReference
    Capelinho capelinho;

    public UserDTO getUserDTO() {
        if (capelinho == null) {
            return new UserDTO(name, email, locale, null);
        }
        return new UserDTO(name, email, locale, capelinho.getId());
    }
}
