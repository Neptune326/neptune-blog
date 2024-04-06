package com.neptune.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {

    private String id;
    private String createTime;
    private String userName;
    private String nickName;
    private String avatar;
    private String[] roleIds;

}
