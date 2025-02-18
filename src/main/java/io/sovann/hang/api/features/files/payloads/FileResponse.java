package io.sovann.hang.api.features.files.payloads;


import io.sovann.hang.api.constants.APIURLs;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

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
