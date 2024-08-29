package com.github.paicoding.forum.service.hotproject.service.impl;

import com.github.paicoding.forum.api.model.vo.PageListVo;
import com.github.paicoding.forum.api.model.vo.PageParam;
import com.github.paicoding.forum.api.model.vo.hotproject.dto.HotProjectDTO;
import com.github.paicoding.forum.service.hotproject.conveter.HotProjectConverter;
import com.github.paicoding.forum.service.hotproject.repository.dao.HotProjectDao;
import com.github.paicoding.forum.service.hotproject.repository.entity.HotProjectDo;
import com.github.paicoding.forum.service.hotproject.service.HotProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class HotProjectServiceImpl implements HotProjectService {

    @Autowired
    private HotProjectDao hotProjectDao;

    @Override
    public PageListVo<HotProjectDTO> listHotProject(PageParam page) {
        List<HotProjectDo> hotProjectDoList = hotProjectDao.listHotProjects(page);
        return buildArticleListVo(hotProjectDoList, page.getPageSize());
    }

    public PageListVo<HotProjectDTO> buildArticleListVo(List<HotProjectDo> records, long pageSize) {
        List<HotProjectDTO> result = IntStream.range(0, records.size())
                .mapToObj(index -> fillArticleRelatedInfo(records.get(index), index))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return PageListVo.newVo(result, pageSize);
    }

    private HotProjectDTO fillArticleRelatedInfo(HotProjectDo hotProjectDo, int index){
        return HotProjectConverter.toDto(hotProjectDo, index);
    }
}
