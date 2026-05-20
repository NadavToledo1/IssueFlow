package com.issueflow.util;

import com.issueflow.common.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class FileValidator {

    @Value("${file.max-size-bytes}")
    private long maxSize;

    @Value("${file.allowed-content-types}")
    private List<String> allowedTypes;

    public void validate(MultipartFile file) {
        if (file.getSize() > maxSize) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "File exceeds max size of 10MB");
        }
        if (!allowedTypes.contains(file.getContentType())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported file type: " + file.getContentType());
        }
    }
}
