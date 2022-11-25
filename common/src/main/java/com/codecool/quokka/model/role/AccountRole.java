package com.codecool.quokka.model.role;

import com.codecool.quokka.model.account.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AccountRole {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    @Column(nullable = true)
    private String description;
    @ManyToMany(mappedBy = "roles", cascade = CascadeType.ALL)
    private Set<Account> accounts;

    public AccountRole(String name, String description) {
        this.name = name;
        this.description = description;
        this.accounts = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountRole that = (AccountRole) o;
        return name.equals(that.name) && description.equals(that.description) && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id);
    }
}
