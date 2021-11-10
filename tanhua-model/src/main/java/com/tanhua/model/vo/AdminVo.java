package com.tanhua.model.vo;

import com.tanhua.model.domain.Admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminVo {
    private String id;
    private String username;
    private String avatar;

    public static AdminVo init(Admin admin) {
        AdminVo vo = new AdminVo();
        vo.setAvatar(admin.getAvatar());
        vo.setUsername(admin.getUsername());
        vo.setId(admin.getId().toString());
        return vo;
    }

}