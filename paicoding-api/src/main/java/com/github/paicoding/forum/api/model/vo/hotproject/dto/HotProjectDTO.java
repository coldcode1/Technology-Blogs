package com.github.paicoding.forum.api.model.vo.hotproject.dto;

import lombok.Data;

@Data
public class HotProjectDTO {

    /**
     * 项目排序的索引
     */
    private int index;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 项目地址
     */
    private String url;
}
