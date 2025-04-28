package com.bkb.scanner.mapper;

import com.bkb.scanner.dto.CustomerDTO;
import com.bkb.scanner.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerDTO toDTO(Customer entity) {
        if (entity == null) {
            return null;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setBasicNumber(entity.getBasicNumber());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setAddress(entity.getAddress());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setPassport(entity.getPassport());
        dto.setCustomerType(entity.getCustomerType());
        dto.setIncorporationNumber(entity.getIncorporationNumber());
        dto.setRegistrationCountry(entity.getRegistrationCountry());
        dto.setUbos(entity.getUbos());
        dto.setIsPEP(entity.getIsPEP());
        dto.setAmlStatus(entity.getAmlStatus());
        dto.setAmlCheckDate(entity.getAmlCheckDate());
        dto.setAmlScore(entity.getAmlScore());
        dto.setAmlNotes(entity.getAmlNotes());
        dto.setRiskRating(entity.getRiskRating());
        dto.setLifecycleStatus(entity.getLifecycleStatus());
        dto.setLifecycleStatusDate(entity.getLifecycleStatusDate());
        dto.setRelationshipManager(entity.getRelationshipManager());
        dto.setHasOutstandingDocuments(entity.getHasOutstandingDocuments());

        // Map new fields
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setNationality(entity.getNationality());
        dto.setPrimaryContact(entity.getPrimaryContact());
        dto.setBusinessNature(entity.getBusinessNature());

        return dto;
    }

    public Customer toEntity(CustomerDTO dto) {
        if (dto == null) {
            return null;
        }

        Customer entity = new Customer();
        entity.setBasicNumber(dto.getBasicNumber());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setAddress(dto.getAddress());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setPassport(dto.getPassport());
        entity.setCustomerType(dto.getCustomerType());
        entity.setIncorporationNumber(dto.getIncorporationNumber());
        entity.setRegistrationCountry(dto.getRegistrationCountry());
        entity.setUbos(dto.getUbos());
        entity.setIsPEP(dto.getIsPEP());
        entity.setAmlStatus(dto.getAmlStatus());
        entity.setAmlCheckDate(dto.getAmlCheckDate());
        entity.setAmlScore(dto.getAmlScore());
        entity.setAmlNotes(dto.getAmlNotes());
        entity.setRiskRating(dto.getRiskRating());
        entity.setLifecycleStatus(dto.getLifecycleStatus());
        entity.setLifecycleStatusDate(dto.getLifecycleStatusDate());
        entity.setRelationshipManager(dto.getRelationshipManager());
        entity.setHasOutstandingDocuments(dto.getHasOutstandingDocuments());

        // Map new fields
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setNationality(dto.getNationality());
        entity.setPrimaryContact(dto.getPrimaryContact());
        entity.setBusinessNature(dto.getBusinessNature());

        return entity;
    }

    public void updateEntityFromDTO(CustomerDTO dto, Customer entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Only update non-null fields from the DTO
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getAddress() != null) entity.setAddress(dto.getAddress());
        if (dto.getPhoneNumber() != null) entity.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getPassport() != null) entity.setPassport(dto.getPassport());
        if (dto.getCustomerType() != null) entity.setCustomerType(dto.getCustomerType());
        if (dto.getIncorporationNumber() != null) entity.setIncorporationNumber(dto.getIncorporationNumber());
        if (dto.getRegistrationCountry() != null) entity.setRegistrationCountry(dto.getRegistrationCountry());
        if (dto.getUbos() != null) entity.setUbos(dto.getUbos());
        if (dto.getIsPEP() != null) entity.setIsPEP(dto.getIsPEP());
        if (dto.getAmlStatus() != null) entity.setAmlStatus(dto.getAmlStatus());
        if (dto.getAmlCheckDate() != null) entity.setAmlCheckDate(dto.getAmlCheckDate());
        if (dto.getAmlScore() != null) entity.setAmlScore(dto.getAmlScore());
        if (dto.getAmlNotes() != null) entity.setAmlNotes(dto.getAmlNotes());
        if (dto.getRiskRating() != null) entity.setRiskRating(dto.getRiskRating());
        if (dto.getLifecycleStatus() != null) entity.setLifecycleStatus(dto.getLifecycleStatus());
        if (dto.getLifecycleStatusDate() != null) entity.setLifecycleStatusDate(dto.getLifecycleStatusDate());
        if (dto.getRelationshipManager() != null) entity.setRelationshipManager(dto.getRelationshipManager());
        if (dto.getHasOutstandingDocuments() != null) entity.setHasOutstandingDocuments(dto.getHasOutstandingDocuments());
        if (dto.getDateOfBirth() != null) entity.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getNationality() != null) entity.setNationality(dto.getNationality());
        if (dto.getPrimaryContact() != null) entity.setPrimaryContact(dto.getPrimaryContact());
        if (dto.getBusinessNature() != null) entity.setBusinessNature(dto.getBusinessNature());
    }
}