package com.springbot.recyclemapbot.serviceImplementation;

import com.springbot.recyclemapbot.model.User;
import com.springbot.recyclemapbot.repository.UserRepository;
import com.springbot.recyclemapbot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void save(User user) {
        userRepository.save(user);
    }
}
