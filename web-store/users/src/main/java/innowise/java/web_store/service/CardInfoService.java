package innowise.java.web_store.service;

import innowise.java.web_store.dto.request.CardInfoRequest;
import innowise.java.web_store.dto.response.CardInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardInfoService {

    CardInfoResponse create(CardInfoRequest dto);

    CardInfoResponse getById(Long id);

    Page<CardInfoResponse> getAll(Pageable pageable);

    void delete(Long id);
}
