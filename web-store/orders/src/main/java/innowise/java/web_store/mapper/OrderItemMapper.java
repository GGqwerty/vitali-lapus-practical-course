package innowise.java.web_store.mapper;

import innowise.java.web_store.dto.request.OrderItemRequest;
import innowise.java.web_store.dto.response.OrderItemResponse;
import innowise.java.web_store.entity.Item;
import innowise.java.web_store.entity.OrderItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderItemMapper {

    @Mapping(target = "item", source = "itemId", qualifiedByName = "idToItem")
    OrderItem toEntity(OrderItemRequest dto);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "price", source = "item.price")
    OrderItemResponse toResponse(OrderItem orderItem);

    @Mapping(target = "items", source = "items")
    List<OrderItemResponse> mapItems(List<OrderItem> items);

    @Named("idToItem")
    default Item idToItem(Long id) {
        if (id == null) return null;
        Item item = new Item();
        item.setId(id);
        return item;
    }
}
