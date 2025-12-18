package top.amfun.quickinit.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.amfun.quickinit.annotation.LogRecord;
import top.amfun.quickinit.common.PageResponse;
import top.amfun.quickinit.entity.Menu;
import top.amfun.quickinit.entity.RoleMenu;
import top.amfun.quickinit.mapper.MenuMapper;
import top.amfun.quickinit.mapper.RoleMenuMapper;
import top.amfun.quickinit.pojo.qo.MenuPageQuery;

import java.util.List;
import java.util.stream.Collectors;

@Service
class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @LogRecord(type = "menu")
    @Override
    public List<Menu> getMenuList(Long userId) {
        return menuMapper.getMenuListByUserId(userId);
    }

    @Override
    public List<Menu> allMenuList(List<Long> roleIdes) {
        SpringUtil.getBean(RoleMenuService.class);
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(RoleMenu::getRoleId, roleIdes);
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(queryWrapper);
        List<Long> menuIds = roleMenus.stream().map(e -> e.getMenuId()).distinct().collect(Collectors.toList());
        LambdaQueryWrapper<Menu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.eq(Menu::getHidden, Boolean.FALSE);
        menuWrapper.in(Menu::getMenuId, menuIds);
        menuWrapper.orderByAsc(Menu::getSort);
        return menuMapper.selectList(menuWrapper);
    }

    @Override
    public PageResponse<Menu> pageSelect(MenuPageQuery query) {
        Page<Menu> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(query.getTitle())) {
            queryWrapper.like(Menu::getTitle, query.getTitle());
        }
        queryWrapper.orderByDesc(Menu::getMenuId);
        return PageResponse.restPage(menuMapper.selectPage(page, queryWrapper));
    }
}
