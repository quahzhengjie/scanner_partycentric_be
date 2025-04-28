package com.bkb.scanner.mapper;

import com.bkb.scanner.dto.CustomerDocumentDTO;
import com.bkb.scanner.entity.CustomerDocument;
import org.springframework.stereotype.Component;

@Component
public class CustomerDocumentMapper {

    /**
     * Convert CustomerDocument entity to CustomerDocumentDTO
     */
    public CustomerDocumentDTO toDTO(CustomerDocument entity) {
        if (entity == null) {
            return null;
        }

        return CustomerDocumentDTO.builder()
                .id(entity.getId())
                .documentName(entity.getDocumentName())
                .uploadStatus(entity.getUploadStatus().name())
                .uploadDate(entity.getUploadDate())
                .customerBasicNumber(entity.getCustomer().getBasicNumber())
                .customerName(entity.getCustomer().getName())
                .category(entity.getCategory().name())
                .contentType(entity.getContentType())
                .fileContent(entity.getFileContent())
                .build();
    }

    /**
     * Convert CustomerDocumentDTO to CustomerDocument entity
     * Note: This doesn't set the customer relationship which should be handled by the service
     */
    public CustomerDocument toEntity(CustomerDocumentDTO dto) {
        if (dto == null) {
            return null;
        }

        CustomerDocument entity = new CustomerDocument();
        entity.setId(dto.getId());
        entity.setDocumentName(dto.getDocumentName());

        if (dto.getUploadStatus() != null) {
            entity.setUploadStatus(com.bkb.scanner.entity.DocumentStatus.valueOf(dto.getUploadStatus()));
        }

        entity.setUploadDate(dto.getUploadDate());

        if (dto.getCategory() != null) {
            entity.setCategory(com.bkb.scanner.entity.DocumentCategory.valueOf(dto.getCategory()));
        }

        entity.setContentType(dto.getContentType());
        entity.setFileContent(dto.getFileContent());

        return entity;
    }
}