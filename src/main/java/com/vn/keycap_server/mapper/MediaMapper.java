package com.vn.keycap_server.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vn.keycap_server.dto.request.media.SaveMediaRequest;
import com.vn.keycap_server.dto.response.media.SavedMediaResponse;
import com.vn.keycap_server.modal.Media;

@Mapper(componentModel = "spring")
public interface MediaMapper {

    @Mapping(target = "resourceType", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "uploadedBy", ignore = true)
    Media toMedia(SaveMediaRequest request);

    @Mapping(target = "url", source = "secureUrl")
    SavedMediaResponse toSavedMediaResponse(Media media);

    List<SavedMediaResponse> toSavedMediaResponses(List<Media> medias);
}
