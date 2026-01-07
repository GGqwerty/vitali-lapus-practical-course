package innowise.java.web_store.mapper;

import innowise.java.web_store.dto.request.ItemRequest;
import innowise.java.web_store.dto.response.ItemResponse;
import innowise.java.web_store.entity.Item;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ItemMapper {

    Item toEntity(ItemRequest dto);

    ItemResponse toDTO(Item entity);

    @Mapping(target = "items", source = "items")
    List<ItemResponse> mapItems(List<Item> items);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ItemRequest dto, @MappingTarget Item entity);
}
