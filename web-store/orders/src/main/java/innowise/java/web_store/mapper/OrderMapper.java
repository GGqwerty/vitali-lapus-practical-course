package innowise.java.web_store.mapper;

import innowise.java.web_store.dto.request.OrderItemRequest;
import innowise.java.web_store.dto.request.OrderRequest;
import innowise.java.web_store.dto.response.OrderResponse;
import innowise.java.web_store.entity.Item;
import innowise.java.web_store.entity.Order;
import innowise.java.web_store.entity.OrderItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = OrderItemMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {

    @Mapping(target = "orderItems", source = "items", qualifiedByName = "itemIdsToEntities")
    Order toEntity(OrderRequest orderRequest);

    @Mapping(target = "items", source = "orderItems")
    OrderResponse toResponse(Order order);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "orderItems", source = "items", qualifiedByName = "itemIdsToEntities")
    void updateEntityFromRequest(OrderRequest orderRequest, @MappingTarget Order order);

    @Named("itemIdsToEntities")
    default List<OrderItem> itemIdsToEntities(List<OrderItemRequest> ids) {
        if (ids == null) return null;
        return ids.stream()
                .map(id -> {
                    Item item = new Item();
                    item.setId(id.getItemId());
                    OrderItem orderItem = new OrderItem();
                    orderItem.setItem(item);
                    orderItem.setQuantity(id.getQuantity());
                    return orderItem;
                })
                .toList();
    }

    @AfterMapping
    default void setOrderInOrderItems(@MappingTarget Order order) {
        if (order.getOrderItems() != null) {
            for (OrderItem oi : order.getOrderItems()) {
                oi.setOrder(order);
            }
        }
    }
}
