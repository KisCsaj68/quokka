package com.codecool.quokka.model.account;

import com.codecool.quokka.model.role.AccountRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.*;

@Entity
@AllArgsConstructor
public class Account implements UserDetails {

    @Id
    @GeneratedValue(generator = "uuid")
    @JsonIgnore
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid")
    private UUID id;
    private String fullName;
    private String userName;
    private String emailAddress;

    private String password;
    private final boolean isAccountNonExpired;
    private final boolean isAccountNonLocked;
    private final boolean isCredentialsNonExpired;
    private final boolean isEnabled;

    @JsonIgnore
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);
    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private final Set<AccountRole> roles;

    private static AccountRole traderRole =  new AccountRole("TRADER", "Trading");

    /**
     * Constructor to create Account instance data in JSON.
     */
    public Account(@JsonProperty("full_name") String fullName, @JsonProperty("email_address") String emailAddress, @JsonProperty("user_name") String userName, @JsonProperty("password") String password) {
        this.fullName = fullName;
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.password = password;
        this.roles = Sets.newHashSet(traderRole);
        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;

    }

    /**
     * Constructor to create Account instance while testing does not receive data in JSON.
     */
    public Account(String fullName, String emailAddress, String userName, String password, UUID id) {
        this.id = id;
        this.fullName = fullName;
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.password = password;
        this.roles = Sets.newHashSet(traderRole);
        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;
    }

    public Account() {
        this.roles = Sets.newHashSet(traderRole);;
        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;

    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }


//    public String getPassword() {
//        return password;
//    }


    public void setFullName(String name) {
        this.fullName = name;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserId(UUID userId) {
        this.id = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void encryptPassword() {
        this.password = bCryptPasswordEncoder.encode(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id.equals(account.id) && userName.equals(account.userName) && emailAddress.equals(account.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, emailAddress);
    }

    @Override
    public String toString() {
        return "Account{" + "id=" + id + ", fullName='" + fullName + '\'' + ", userName='" + userName + '\'' + ", emailAddress='" + emailAddress + '\'' + ", password='" + password + '\'' + '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authList = new HashSet<>();
        for (AccountRole role : this.roles) {
            authList.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }
        return authList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}

