package innowise.java.web_store.controller;

import innowise.java.web_store.dto.request.CardInfoRequest;
import innowise.java.web_store.dto.response.CardInfoResponse;
import innowise.java.web_store.service.CardInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardInfoController {

    private final CardInfoService cardInfoService;

    @PostMapping
    public ResponseEntity<CardInfoResponse> createCard(@Validated @RequestBody CardInfoRequest request) {
        CardInfoResponse response = cardInfoService.create(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardInfoResponse> getCardById(@PathVariable Long id) {
        CardInfoResponse response = cardInfoService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<CardInfoResponse>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CardInfoResponse> cards = cardInfoService.getAll(pageable);
        return ResponseEntity.ok(cards);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardInfoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

