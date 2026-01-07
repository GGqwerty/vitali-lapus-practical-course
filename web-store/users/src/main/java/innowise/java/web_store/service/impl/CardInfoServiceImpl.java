package innowise.java.web_store.service.impl;

import innowise.java.web_store.dto.request.CardInfoRequest;
import innowise.java.web_store.dto.response.CardInfoResponse;
import innowise.java.web_store.entity.CardInfo;
import innowise.java.web_store.entity.User;
import innowise.java.web_store.exception.ApiException;
import innowise.java.web_store.exception.ApiExceptionType;
import innowise.java.web_store.mapper.CardInfoMapper;
import innowise.java.web_store.repository.CardInfoRepository;
import innowise.java.web_store.repository.UserRepository;
import innowise.java.web_store.service.CardInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardInfoServiceImpl implements CardInfoService {

    private final CardInfoRepository cardRepository;
    private final UserRepository userRepository;
    private final CardInfoMapper cardInfoMapper;

    @Transactional
    @Override
    public CardInfoResponse create(CardInfoRequest dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ApiException(ApiExceptionType.ERR_NOT_FOUND));

        CardInfo entity = cardInfoMapper.toEntity(dto);
        entity.setUser(user);

        return cardInfoMapper.toDTO(cardRepository.save(entity));
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "cards", key = "#id")
    public CardInfoResponse getById(Long id) {
        return cardRepository.findByIdNative(id)
                .map(cardInfoMapper::toDTO)
                .orElseThrow(() -> new ApiException(ApiExceptionType.ERR_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CardInfoResponse> getAll(Pageable pageable) {
        return cardRepository.findAllByOrderByExpirationDateAscHolderAsc(pageable)
                .map(cardInfoMapper::toDTO);
    }

    @Transactional
    @Override
    @CacheEvict(value = "cards", key = "#id")
    public void delete(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new ApiException(ApiExceptionType.ERR_NOT_FOUND);
        }
        cardRepository.deleteByIdJPQL(id);
    }
}
