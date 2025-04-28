package com.bkb.scanner.mapper;

import com.bkb.scanner.dto.AccountDocumentDTO;
import com.bkb.scanner.entity.AccountDocument;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Base64;

@Component
public class AccountDocumentMapper {

    public AccountDocumentDTO toDTO(AccountDocument entity) {
        if (entity == null) {
            return null;
        }

        String base64Content = null;
        if (entity.getFileContent() != null) {
            base64Content = Base64.getEncoder().encodeToString(entity.getFileContent());
        }

        return AccountDocumentDTO.builder()
                .id(entity.getId())
                .documentName(entity.getDocumentName())
                .uploadStatus(entity.getUploadStatus())
                .uploadDate(entity.getUploadDate())
                .contentType(entity.getContentType())
                .base64Content(base64Content)
                .category(entity.getCategory())
                .build();
    }

    public AccountDocument toEntity(AccountDocumentDTO dto) {
        if (dto == null) {
            return null;
        }

        AccountDocument entity = new AccountDocument();
        entity.setId(dto.getId());
        entity.setDocumentName(dto.getDocumentName());
        entity.setUploadStatus(dto.getUploadStatus());
        entity.setUploadDate(dto.getUploadDate());
        entity.setContentType(dto.getContentType());
        entity.setCategory(dto.getCategory());

        if (dto.getBase64Content() != null && !dto.getBase64Content().isEmpty()) {
            entity.setFileContent(Base64.getDecoder().decode(dto.getBase64Content()));
        }

        return entity;
    }

    public AccountDocument updateEntityFromDTO(AccountDocumentDTO dto, AccountDocument entity) {
        if (dto == null || entity == null) {
            return entity;
        }

        if (dto.getDocumentName() != null) entity.setDocumentName(dto.getDocumentName());
        if (dto.getUploadStatus() != null) entity.setUploadStatus(dto.getUploadStatus());
        if (dto.getUploadDate() != null) entity.setUploadDate(dto.getUploadDate());
        if (dto.getContentType() != null) entity.setContentType(dto.getContentType());
        if (dto.getCategory() != null) entity.setCategory(dto.getCategory());

        if (dto.getBase64Content() != null && !dto.getBase64Content().isEmpty()) {
            entity.setFileContent(Base64.getDecoder().decode(dto.getBase64Content()));

            if (entity.getUploadDate() == null) {
                entity.setUploadDate(LocalDate.now());
            }
        }

        return entity;
    }
}