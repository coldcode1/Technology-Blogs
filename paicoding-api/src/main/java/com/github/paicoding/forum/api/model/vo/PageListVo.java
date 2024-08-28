package com.github.paicoding.forum.api.model.vo;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author YiHui
 * @date 2022/9/4
 */
@Data
public class PageListVo<T> {

    /**
     * 用户列表
     */
    List<T> list;

    /**
     * 是否有更多
     */
    private Boolean hasMore;

    public static <T> PageListVo<T> emptyVo() {
        PageListVo<T> vo = new PageListVo<>();
        vo.setList(Collections.emptyList());
        vo.setHasMore(false);
        return vo;
    }

    public static <T> PageListVo<T> newVo(List<T> list, long pageSize) {
        PageListVo<T> vo = new PageListVo<>();
        // 一种偷懒的做法，如果看到当前获取文章列表数量能达到和请求的数量一样，那么说明还有更多。这其实并不多好，但是这里先这样处理
        vo.setList(Optional.ofNullable(list).orElse(Collections.emptyList()));
        vo.setHasMore(vo.getList().size() == pageSize);
        return vo;
    }
}
