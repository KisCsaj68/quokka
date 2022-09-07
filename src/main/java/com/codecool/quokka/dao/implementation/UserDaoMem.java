package com.codecool.quokka.dao.implementation;

import com.codecool.quokka.dao.UserDao;
import com.codecool.quokka.model.User;
import com.codecool.quokka.model.UserDto;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository("inMemoUserDao")
public class UserDaoMem implements UserDao {

    private static Set<User> DB = new HashSet<>() {{
        UUID id = UUID.fromString("b462290f-4006-4d71-8a39-e956e245ede8");
        add(new User("Test User", "test@asd.com", "TestUser", "asd", id));
    }};

    @Override
    public UserDto addUser(String name, String userName, String emailAddress, String passWord) {
        User newUser = new User(name, emailAddress, userName, passWord);
        DB.add(newUser);
        return new UserDto(userName, newUser.getId(), name, emailAddress);
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
                .map(e -> new UserDto(e.getUserName(), e.getId(), e.getFullName(), e.getEmailAddress()));
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

    @Override
    public Optional<UserDto> updateUser(UUID id, HashMap<String, String> fields) {
        Optional<User> user = DB.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
        if (user.isPresent()) {
            User actualUser = user.get();
            for (String item : fields.keySet()) {
                switch (item) {
                    case "emailAddress":
                        actualUser.setEmailAddress(fields.get("emailAddress"));
                        break;
                    case "fullName":
                        actualUser.setFullName(fields.get("fullName"));
                    case "passWord":
                        actualUser.setPassWord(fields.get("passWord"));
                }
            }
            Optional<UserDto> actualDto = Optional.of(new UserDto(actualUser.getUserName(), actualUser.getId(),
                    actualUser.getFullName(), actualUser.getEmailAddress()));
            return actualDto;
        }
        return Optional.empty();
    }
}
