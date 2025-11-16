package innowise.java.web_store.service;

import innowise.java.web_store.dto.request.ItemRequest;
import innowise.java.web_store.dto.response.ItemResponse;
import innowise.java.web_store.entity.Item;
import innowise.java.web_store.mapper.ItemMapper;
import innowise.java.web_store.repository.ItemRepository;
import innowise.java.web_store.service.impl.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;
    private ItemRequest itemRequest;
    private ItemResponse itemResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        item = new Item();
        item.setId(1L);
        item.setName("TestItem");
        item.setPrice(BigDecimal.valueOf(100));

        itemRequest = new ItemRequest();
        itemRequest.setName("TestItem");
        itemRequest.setPrice(BigDecimal.valueOf(100));

        itemResponse = new ItemResponse();
        itemResponse.setId(1L);
        itemResponse.setName("TestItem");
        itemResponse.setPrice(BigDecimal.valueOf(100));
    }

    @Test
    void testGetItemById() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toDTO(item)).thenReturn(itemResponse);

        ItemResponse response = itemService.getItemById(1L);
        assertEquals("TestItem", response.getName());
        verify(itemRepository).findById(1L);
    }

    @Test
    void testCreateItem() {
        when(itemMapper.toEntity(itemRequest)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toDTO(item)).thenReturn(itemResponse);

        ItemResponse response = itemService.createItem(itemRequest);
        assertNotNull(response);
        verify(itemRepository).save(item);
    }

    @Test
    void testUpdateItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        doNothing().when(itemMapper).updateEntityFromDTO(itemRequest, item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toDTO(item)).thenReturn(itemResponse);

        ItemResponse response = itemService.updateItem(1L, itemRequest);
        assertEquals("TestItem", response.getName());
        verify(itemRepository).save(item);
    }

    @Test
    void testDeleteItem() {
        when(itemRepository.existsById(1L)).thenReturn(true);
        doNothing().when(itemRepository).deleteById(1L);

        assertDoesNotThrow(() -> itemService.deleteItem(1L));
        verify(itemRepository).deleteById(1L);
    }
}
