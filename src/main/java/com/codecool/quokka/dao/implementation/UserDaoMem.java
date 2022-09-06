package com.codecool.quokka.dao.implementation;

import com.codecool.quokka.dao.UserDao;
import com.codecool.quokka.model.User;
import com.codecool.quokka.model.UserDto;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository("inMemoUserDao")
public class UserDaoMem implements UserDao {

    private static Set<User> DB = new HashSet<>();
    @Override
    public UserDto addUser(String name, String userName, String emailAddress, String passWord) {
        User newUser = new User(name, emailAddress, userName, passWord);
        DB.add(newUser);
        return new UserDto(userName,newUser.getId(), name, emailAddress);
    }

    @Override
    public UserDto addUser(User user) {
        DB.add(user);
        return new UserDto(user.getUserName(), user.getId(), user.getFullName(), user.getEmailAddress());
    }

    @Override
    public Set<UserDto> getAllUser() {
        return this.DB.stream()
                .map(e -> new UserDto(e.getUserName(), e.getId(), e.getFullName(), e.getEmailAddress()))
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<UserDto> getUser(UUID id) {
        Optional<UserDto> user = DB.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .map(e -> new UserDto(e.getUserName(),e.getId(), e.getFullName(), e.getEmailAddress()));
        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        Optional<User> user = DB.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
        if (user.isPresent()) {
            DB.remove(user.get());
        }
    }
}
