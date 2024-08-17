package com.github.paicoding.forum.service.hotproject.service;

import com.github.paicoding.forum.api.model.vo.PageListVo;
import com.github.paicoding.forum.api.model.vo.PageParam;
import com.github.paicoding.forum.api.model.vo.hotproject.dto.HotProjectDTO;

/**
 * @author wcd
 */
public interface HotProjectService {
    PageListVo<HotProjectDTO> listHotProject(PageParam pageParam);

}
