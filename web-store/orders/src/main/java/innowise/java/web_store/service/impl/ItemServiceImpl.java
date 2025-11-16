package innowise.java.web_store.service.impl;

import innowise.java.web_store.dto.request.ItemRequest;
import innowise.java.web_store.dto.response.ItemResponse;
import innowise.java.web_store.entity.Item;
import innowise.java.web_store.exception.ApiException;
import innowise.java.web_store.exception.ApiExceptionType;
import innowise.java.web_store.mapper.ItemMapper;
import innowise.java.web_store.repository.ItemRepository;
import innowise.java.web_store.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "items", key = "#id")
    public ItemResponse getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiExceptionType.ERR_NOT_FOUND));
        return itemMapper.toDTO(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponse> getAllItems() {
        List<Item> items = itemRepository.findAll();
        return itemMapper.mapItems(items);
    }

    @Override
    @Transactional
    public ItemResponse createItem(ItemRequest itemRequest) {
        Item item = itemMapper.toEntity(itemRequest);
        Item savedItem = itemRepository.save(item);
        return itemMapper.toDTO(savedItem);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "items", key = "#id"),
    })
    public ItemResponse updateItem(Long id, ItemRequest itemRequest) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiExceptionType.ERR_NOT_FOUND));
        itemMapper.updateEntityFromDTO(itemRequest, item);
        Item updatedItem = itemRepository.save(item);
        return itemMapper.toDTO(updatedItem);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "items", key = "#id"),
    })
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ApiException(ApiExceptionType.ERR_NOT_FOUND);
        }
        itemRepository.deleteById(id);
    }
}