package innowise.java.web_store.service;

import innowise.java.web_store.dto.request.ItemRequest;
import innowise.java.web_store.dto.response.ItemResponse;

import java.util.List;

public interface ItemService {

    ItemResponse getItemById(Long id);

    List<ItemResponse> getAllItems();

    ItemResponse createItem(ItemRequest itemRequest);

    ItemResponse updateItem(Long id, ItemRequest itemRequest);

    void deleteItem(Long id);
}
