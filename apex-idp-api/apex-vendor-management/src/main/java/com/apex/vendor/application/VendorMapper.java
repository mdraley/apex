package com.apex.vendor.application;

import com.apex.vendor.domain.Vendor;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VendorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "domainEventsList", ignore = true)
    @Mapping(target = "status", ignore = true)
    Vendor toEntity(CreateVendorCommand command);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVendorFromCommand(UpdateVendorCommand command, @MappingTarget Vendor vendor);
}
