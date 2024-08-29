package com.github.paicoding.forum.service.hotproject.repository.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.paicoding.forum.api.model.vo.PageParam;
import com.github.paicoding.forum.service.hotproject.repository.entity.HotProjectDo;
import com.github.paicoding.forum.service.hotproject.repository.mapper.HotProjectMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HotProjectDao extends ServiceImpl<HotProjectMapper,HotProjectDo> {

    /**
     * 获取热门项目列表
     * @param pageParam
     * @return
     */
    public List<HotProjectDo> listHotProjects(PageParam pageParam){
        return lambdaQuery()
                .orderByDesc(HotProjectDo::getUpdateTime)
                .last(PageParam.getLimitSql(pageParam))
                .list();
    }
}
