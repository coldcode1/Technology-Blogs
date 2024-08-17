package com.github.paicoding.forum.web.front.hotproject.view;

import com.github.paicoding.forum.api.model.vo.PageListVo;
import com.github.paicoding.forum.api.model.vo.PageParam;
import com.github.paicoding.forum.api.model.vo.hotproject.dto.HotProjectDTO;
import com.github.paicoding.forum.api.model.vo.recommend.SideBarDTO;
import com.github.paicoding.forum.service.hotproject.service.HotProjectService;
import com.github.paicoding.forum.service.sidebar.service.SidebarService;
import com.github.paicoding.forum.web.front.hotproject.vo.HotProjectVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/hotproject")
public class HotProjectViewControl {

    @Autowired
    private HotProjectService hotProjectService;

    @Autowired
    private SidebarService sidebarService;

    @GetMapping(path = {"list", "/", "", "home"})
    public String list(Model model) {
        PageListVo<HotProjectDTO> hotProjects = hotProjectService.listHotProject(PageParam.newPageInstance());
        List<SideBarDTO> sidebars = sidebarService.queryColumnSidebarList();
        HotProjectVo vo = new HotProjectVo();
        vo.setHotprojects(hotProjects);
        vo.setSideBarItems(sidebars);
        model.addAttribute("vo", vo);
        return "views/hotproject-home/index";
    }
}
