package com.github.paicoding.forum.web.front.hotproject.vo;

import com.github.paicoding.forum.api.model.vo.PageListVo;
import com.github.paicoding.forum.api.model.vo.hotproject.dto.HotProjectDTO;
import com.github.paicoding.forum.api.model.vo.recommend.SideBarDTO;
import lombok.Data;

import java.util.List;

@Data
public class HotProjectVo {
    /**
     * 专栏列表
     */
    private PageListVo<HotProjectDTO> hotprojects;

    /**
     * 侧边栏信息
     */
    private List<SideBarDTO> sideBarItems;
}
