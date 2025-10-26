package innowise.java.web_store.service.impl;

import innowise.java.web_store.dto.request.UserRequest;
import innowise.java.web_store.dto.response.UserResponse;
import innowise.java.web_store.entity.UserEntity;
import innowise.java.web_store.exception.ApiException;
import innowise.java.web_store.exception.ApiExceptionType;
import innowise.java.web_store.mapper.UserMapper;
import innowise.java.web_store.repository.UserRepository;
import innowise.java.web_store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserResponse create(UserRequest dto) {
        UserEntity entity = userMapper.toEntity(dto);
        return userMapper.toDTO(userRepository.save(entity));
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "users", key = "#id")
    public UserResponse getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ApiException(ApiExceptionType.ERR_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "users", key = "#email")
    public UserResponse getByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ApiException(ApiExceptionType.ERR_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAllByOrderBySurnameAscNameAsc(pageable)
                .map(userMapper::toDTO);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "users", key = "#result.email", condition = "#result != null")
    })
    public UserResponse update(Long id, UserRequest dto) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiExceptionType.ERR_NOT_FOUND));
        userMapper.updateEntityFromDTO(dto, entity);
        return userMapper.toDTO(userRepository.save(entity));
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "users", key = "#userEmail")
    })
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ApiException(ApiExceptionType.ERR_NOT_FOUND);
        }
        userRepository.deleteByIdJPQL(id);
    }
}

