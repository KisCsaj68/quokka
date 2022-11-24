package com.codecool.quokka.dao.role;

import com.codecool.quokka.model.role.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRoleDao extends JpaRepository<AccountRole, Long> {
    Optional<AccountRole> getAccountRoleByName(String name);
}
