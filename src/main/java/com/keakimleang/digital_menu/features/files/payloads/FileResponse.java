package com.keakimleang.digital_menu.features.files.payloads;


import com.keakimleang.digital_menu.constants.*;
import java.io.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class FileResponse implements Serializable {
    private String name;
    private String url;
    private UUID createdBy;

    public static FileResponse fromEntity(String filename) {
        FileResponse fileResponse = new FileResponse();
        fileResponse.setName(filename);
        fileResponse.setUrl(APIURLs.FILE + "/" + filename);
        try {
            fileResponse.setCreatedBy(UUID.fromString(filename.split("_")[0]));
        } catch (Exception e) {
            fileResponse.setCreatedBy(null);
        }
        return fileResponse;
    }

    public static List<FileResponse> fromEntities(List<String> filenames) {
        return filenames.stream()
                .map(FileResponse::fromEntity)
                .toList();
    }
}
