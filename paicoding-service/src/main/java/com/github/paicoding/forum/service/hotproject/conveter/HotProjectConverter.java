package com.github.paicoding.forum.service.hotproject.conveter;

import com.github.paicoding.forum.api.model.vo.hotproject.dto.HotProjectDTO;

import com.github.paicoding.forum.service.hotproject.repository.entity.HotProjectDo;

public class HotProjectConverter {

    public static HotProjectDTO toDto(HotProjectDo hotProjectDo, int index) {
        if (hotProjectDo == null) {
            return null;
        }
        HotProjectDTO hotProjectDTO = new HotProjectDTO();
        hotProjectDTO.setIndex(index+1);
        hotProjectDTO.setName(hotProjectDo.getName());
        hotProjectDTO.setDescription(hotProjectDo.getDescription());
        hotProjectDTO.setUrl(hotProjectDo.getUrl());
        return hotProjectDTO;
    }
}
