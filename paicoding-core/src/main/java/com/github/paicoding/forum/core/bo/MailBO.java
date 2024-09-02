package com.github.paicoding.forum.core.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailBO {
    /**
     * 目标邮箱地址
     */
    private String to;

    /**
     * 标题不能为空
     */
    private String title;

    /**
     * 正文不能为空
     */
    private String content;

    /**
     * 消息id
     */
    private String msgId;

}
