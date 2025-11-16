package innowise.java.web_store.mapper;

import innowise.java.web_store.dto.request.OrderRequest;
import innowise.java.web_store.dto.response.OrderResponse;
import innowise.java.web_store.entity.Item;
import innowise.java.web_store.entity.Order;
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
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {

    @Mapping(target = "items", source = "itemIds", qualifiedByName = "itemIdsToEntities")
    Order toEntity(OrderRequest orderRequest);

    OrderResponse toResponse(Order order);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "items", source = "itemIds", qualifiedByName = "itemIdsToEntities")
    void updateEntityFromRequest(OrderRequest orderRequest, @MappingTarget Order order);

    @Named("itemIdsToEntities")
    default List<Item> itemIdsToEntities(List<Long> ids) {
        if (ids == null) return null;
        return ids.stream()
                .map(id -> {
                    Item item = new Item();
                    item.setId(id);
                    return item;
                })
                .toList();
    }
}
