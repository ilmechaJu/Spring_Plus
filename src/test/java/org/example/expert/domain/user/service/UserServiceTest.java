package org.example.expert.domain.user.service;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;
@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Test
    public void createUserDate() {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            String nickname = String.valueOf(i);
            userList.add(new User(nickname));
        }
        for (int i = 0; i < userList.size(); i += 1000) {
            int endIndex = Math.min(i + 1000, userList.size());
            userRepository.saveAll(userList.subList(i, endIndex));
        }
    }
}