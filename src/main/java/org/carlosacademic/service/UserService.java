package org.carlosacademic.service;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.carlosacademic.domain.UserDTO;
import org.carlosacademic.domain.exceptions.InvalidMessageException;
import org.carlosacademic.mapper.UserMapper;
import org.carlosacademic.repositories.UserRepository;
import org.carlosacademic.table.DUser;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    public void register(UserDTO user, LambdaLogger logger) {
        if (user != null){
            DUser dUser = UserMapper.toDUser(user);
            userRepository.saveIfNotExist(dUser, logger);
            return;
        }
        throw new InvalidMessageException("UserDTO is null");
    }
}
