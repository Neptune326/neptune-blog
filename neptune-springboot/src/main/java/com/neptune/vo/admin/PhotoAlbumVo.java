package com.neptune.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoAlbumVo {

    private String id;
    private String status;
    private String name;
    private String info;
    private String cover;

}
