package top.amfun.quickinit.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import top.amfun.quickinit.common.UserDetailsImpl;
import top.amfun.quickinit.common.exception.CommonErrorMessageEnum;
import top.amfun.quickinit.config.ExceptionAsserts;
import top.amfun.quickinit.config.JwtTokenUtil;
import top.amfun.quickinit.entity.Permission;
import top.amfun.quickinit.entity.Role;
import top.amfun.quickinit.entity.User;
import top.amfun.quickinit.mapper.RoleMapper;
import top.amfun.quickinit.mapper.UserMapper;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private RoleMapper roleMapper;
    @Resource
    private RolePermissionService rolePermissionService;

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> lambda = new LambdaQueryWrapper<>();
        lambda.eq(User::getUsername, username);
        List<User> users = baseMapper.selectList(lambda);
        if (ObjectUtil.isNotNull(users) && !users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }

    @Override
    public User register(User user) {
        LambdaQueryWrapper<User> lambda = new LambdaQueryWrapper<>();
        lambda.eq(User::getUsername, user.getUsername());
        int i = baseMapper.selectCount(lambda);
        if (i > 0) {
            throw CommonErrorMessageEnum.DATA_CONFLICT.businessException("用户名已存在");
        }
        user.setUserId(IdWorker.getId());
        baseMapper.insert(user);
        return user;
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        //密码需要客户端加密后传递
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if(!password.equals(userDetails.getPassword())){
                ExceptionAsserts.fail("用户名或者密码不正确");
            }
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (Exception e) {
            log.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    @Override
    public UserDetails loadUserByUsername(String username){
        //获取用户信息
        User user = getUserByUsername(username);
        if (user != null) {
            List<Role> roleList = getRoleList(user.getUserId());
            List<Permission> permissions = rolePermissionService.allPermissions(roleList.stream().map(Role::getRoleId).collect(Collectors.toList()));
            return new UserDetailsImpl(user, roleList, permissions);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }

    @Override
    public List<Role> getRoleList(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }
}
