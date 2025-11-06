package innowise.java.web_store.service;

import innowise.java.web_store.dto.request.CardInfoRequest;
import innowise.java.web_store.dto.response.CardInfoResponse;
import innowise.java.web_store.entity.CardInfo;
import innowise.java.web_store.entity.User;
import innowise.java.web_store.exception.ApiException;
import innowise.java.web_store.mapper.CardInfoMapper;
import innowise.java.web_store.repository.CardInfoRepository;
import innowise.java.web_store.repository.UserRepository;
import innowise.java.web_store.service.impl.CardInfoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardInfoServiceTest {

    @Mock
    private CardInfoRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardInfoMapper cardInfoMapper;

    @InjectMocks
    private CardInfoServiceImpl cardInfoService;

    @Test
    void getCardById_found() {
        CardInfo entity = new CardInfo();
        entity.setId(1L);

        CardInfoResponse dto = new CardInfoResponse();
        dto.setId(1L);

        when(cardRepository.findByIdNative(1L)).thenReturn(Optional.of(entity));
        when(cardInfoMapper.toDTO(entity)).thenReturn(dto);

        CardInfoResponse result = cardInfoService.getById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCardById_notFound() {
        when(cardRepository.findByIdNative(1L)).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> cardInfoService.getById(1L));
    }

    @Test
    void getAllCards_withPagination() {
        CardInfo e1 = new CardInfo();
        e1.setId(1L);
        CardInfo e2 = new CardInfo();
        e2.setId(2L);

        List<CardInfo> list = Arrays.asList(e1, e2);
        Pageable pageable = PageRequest.of(0, 2, Sort.by("expirationDate").ascending());
        Page<CardInfo> page = new PageImpl<>(list, pageable, list.size());

        when(cardRepository.findAllByOrderByExpirationDateAscHolderAsc(pageable)).thenReturn(page);
        when(cardInfoMapper.toDTO(e1)).thenReturn(new CardInfoResponse(){ { setId(1L); } });
        when(cardInfoMapper.toDTO(e2)).thenReturn(new CardInfoResponse(){ { setId(2L); } });

        Page<CardInfoResponse> result = cardInfoService.getAll(pageable);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void createCard_success() {
        CardInfoRequest request = new CardInfoRequest();
        request.setUserId(1L);
        request.setHolder("John");
        request.setNumber("1234");
        request.setExpirationDate(LocalDate.of(2030,1,1));

        User user = new User();
        user.setId(1L);

        CardInfo entity = new CardInfo();
        entity.setId(1L);
        entity.setUser(user);

        CardInfoResponse response = new CardInfoResponse();
        response.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardInfoMapper.toEntity(request)).thenReturn(entity);
        when(cardRepository.save(entity)).thenReturn(entity);
        when(cardInfoMapper.toDTO(entity)).thenReturn(response);

        CardInfoResponse result = cardInfoService.create(request);
        assertEquals(1L, result.getId());
    }

    @Test
    void deleteCard_success() {
        when(cardRepository.existsById(1L)).thenReturn(true);
        doNothing().when(cardRepository).deleteByIdJPQL(1L);
        assertDoesNotThrow(() -> cardInfoService.delete(1L));
    }

    @Test
    void deleteCard_notFound() {
        when(cardRepository.existsById(1L)).thenReturn(false);
        assertThrows(ApiException.class, () -> cardInfoService.delete(1L));
    }
}
