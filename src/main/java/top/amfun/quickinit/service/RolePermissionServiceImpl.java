package top.amfun.quickinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.amfun.quickinit.entity.Permission;
import top.amfun.quickinit.entity.RolePermission;
import top.amfun.quickinit.mapper.PermissionMapper;
import top.amfun.quickinit.mapper.RolePermissionMapper;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaoxg
 * @date 2024/6/4 10:24
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService{
    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public List<Permission> allPermissions(List<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        QueryWrapper<RolePermission> qw = new QueryWrapper<>();
        qw.select("DISTINCT permission_id").lambda().in(RolePermission::getRoleId, roleIds);
        List<RolePermission> rolePermissions = baseMapper.selectList(qw);
        List<Long> pIds = rolePermissions.stream().map(RolePermission::getPermissionId).distinct().collect(Collectors.toList());
        return permissionMapper.selectBatchIds(pIds);
    }
}
