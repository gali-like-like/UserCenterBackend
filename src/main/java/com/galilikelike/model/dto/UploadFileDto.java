package com.galilikelike.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileDto {
    @NotNull(message = "不能为空")
    private MultipartFile file;

    @NotNull(message = "不能为空")
    private String fileHash;

}
