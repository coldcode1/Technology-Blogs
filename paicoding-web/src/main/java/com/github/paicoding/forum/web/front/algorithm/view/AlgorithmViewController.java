package com.github.paicoding.forum.web.front.algorithm.view;

import com.github.paicoding.forum.api.model.vo.PageListVo;
import com.github.paicoding.forum.api.model.vo.PageParam;
import com.github.paicoding.forum.api.model.vo.article.dto.ColumnDTO;
import com.github.paicoding.forum.api.model.vo.recommend.SideBarDTO;
import com.github.paicoding.forum.service.sidebar.service.SidebarService;
import com.github.paicoding.forum.web.front.article.vo.ColumnVo;
import com.github.paicoding.forum.web.front.home.helper.IndexRecommendHelper;
import com.github.paicoding.forum.web.front.home.vo.IndexVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wcd
 */
@Controller
@Slf4j
@RequestMapping("/algorithm")
public class AlgorithmViewController {

    @Autowired
    private IndexRecommendHelper indexRecommendHelper;

    @GetMapping(path = {"/", "", "/index", "/login"})
    public String index(Model model, HttpServletRequest request) {
        String activeTab = request.getParameter("category");
        IndexVo vo = indexRecommendHelper.buildIndexVo(activeTab);
        model.addAttribute("vo", vo);
        return "views/home/index";
    }

//    @Autowired
//    private SidebarService sidebarService;
//
//    @GetMapping(path = {"list", "/", "",})
//    public String algorithmShowList(Model model) {
//        PageListVo<ColumnDTO> columns = columnService.listColumn(PageParam.newPageInstance());
//        List<SideBarDTO> sidebars = sidebarService.queryColumnSidebarList();
//        ColumnVo vo = new ColumnVo();
//        vo.setColumns(columns);
//        vo.setSideBarItems(sidebars);
//        model.addAttribute("vo", vo);
//        return "views/column-home/index";
//    }
}
