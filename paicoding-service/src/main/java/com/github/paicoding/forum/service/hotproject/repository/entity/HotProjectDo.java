package com.github.paicoding.forum.service.hotproject.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.paicoding.forum.api.model.entity.BaseDO;
import lombok.Data;

/**
 * @author wcd
 */
@TableName("hot_project")
@Data
public class HotProjectDo extends BaseDO {
    private String name;
    private String description;
    private String url;

}
