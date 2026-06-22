package com.logicarchive.mapper;

import com.logicarchive.dto.request.UserCreateRequest;
import com.logicarchive.dto.response.UserResponse;
import com.logicarchive.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserCreateRequest request) {
        return User.builder()
                .name(request.name())
                .email(request.email())
                .role(request.role())
                .build();
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
