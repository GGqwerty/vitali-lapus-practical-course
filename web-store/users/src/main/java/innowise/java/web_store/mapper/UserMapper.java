package innowise.java.web_store.mapper;

import innowise.java.web_store.dto.request.UserRequest;
import innowise.java.web_store.dto.response.UserResponse;
import innowise.java.web_store.entity.User;
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
public interface UserMapper {

    User toEntity(UserRequest dto);

    UserResponse toDTO(User entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UserRequest dto, @MappingTarget User entity);
}
