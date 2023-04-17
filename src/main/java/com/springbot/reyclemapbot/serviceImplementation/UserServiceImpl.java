package com.springbot.reyclemapbot.serviceImplementation;

import com.springbot.reyclemapbot.model.Points;
import com.springbot.reyclemapbot.model.User;
import com.springbot.reyclemapbot.repository.PointRepository;
import com.springbot.reyclemapbot.repository.UserRepository;
import com.springbot.reyclemapbot.service.UserService;
import lombok.NoArgsConstructor;
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
