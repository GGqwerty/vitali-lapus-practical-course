package innowise.java.web_store.mapper;

import innowise.java.web_store.dto.request.CardInfoRequest;
import innowise.java.web_store.dto.response.CardInfoResponse;
import innowise.java.web_store.entity.CardInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CardInfoMapper {

    CardInfo toEntity(CardInfoRequest dto);

    CardInfoResponse toDTO(CardInfo entity);
}