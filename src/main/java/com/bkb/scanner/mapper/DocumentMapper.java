package com.bkb.scanner.mapper;

import com.bkb.scanner.dto.DocumentDTO;
import com.bkb.scanner.entity.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Base64;

@Component
public class DocumentMapper {

    public DocumentDTO toDTO(Document entity) {
        if (entity == null) {
            return null;
        }

        String base64Content = null;
        if (entity.getFileContent() != null) {
            base64Content = Base64.getEncoder().encodeToString(entity.getFileContent());
        }

        return DocumentDTO.builder()
                .id(entity.getId())
                .filename(entity.getFilename())
                .uploadedFile(base64Content)
                .uploadedName(entity.getUploadedName())
                .expiryDate(entity.getExpiryDate())
                .status(entity.getStatus())
                .category(entity.getCategory())
                .uploadedDate(entity.getUploadedDate())
                .contentType(entity.getContentType())
                .build();
    }

    public Document toEntity(DocumentDTO dto) {
        if (dto == null) {
            return null;
        }

        Document entity = new Document();
        entity.setId(dto.getId());
        entity.setFilename(dto.getFilename());
        entity.setUploadedName(dto.getUploadedName());

        if (dto.getExpiryDate() != null) {
            entity.setExpiryDate(dto.getExpiryDate());
        }

        entity.setStatus(dto.getStatus());
        entity.setCategory(dto.getCategory());

        if (dto.getUploadedDate() != null) {
            entity.setUploadedDate(dto.getUploadedDate());
        }

        entity.setContentType(dto.getContentType());

        if (dto.getUploadedFile() != null && !dto.getUploadedFile().isEmpty()) {
            entity.setFileContent(Base64.getDecoder().decode(dto.getUploadedFile()));
        }

        return entity;
    }

    public Document updateEntityFromDTO(DocumentDTO dto, Document entity) {
        if (dto == null || entity == null) {
            return entity;
        }

        if (dto.getUploadedName() != null) entity.setUploadedName(dto.getUploadedName());
        if (dto.getExpiryDate() != null) entity.setExpiryDate(dto.getExpiryDate());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getContentType() != null) entity.setContentType(dto.getContentType());

        if (dto.getUploadedFile() != null && !dto.getUploadedFile().isEmpty()) {
            entity.setFileContent(Base64.getDecoder().decode(dto.getUploadedFile()));

            if (entity.getUploadedDate() == null) {
                entity.setUploadedDate(LocalDate.now());
            }
        }

        return entity;
    }
}