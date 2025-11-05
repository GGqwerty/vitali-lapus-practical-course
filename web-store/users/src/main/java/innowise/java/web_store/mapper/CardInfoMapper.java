package innowise.java.web_store.mapper;

import innowise.java.web_store.dto.request.CardInfoRequest;
import innowise.java.web_store.dto.response.CardInfoResponse;
import innowise.java.web_store.entity.CardInfoEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CardInfoMapper {

    CardInfoEntity toEntity(CardInfoRequest dto);

    CardInfoResponse toDTO(CardInfoEntity entity);
}