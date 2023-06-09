package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.ucenter.mapper.XcMenuMapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcMenu;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mr.M
 * @version 1.0
 * @description 自定义UserDetailsService用来对接Spring Security
 * @date 2022/9/28 18:09
 */
@Slf4j
@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    XcMenuMapper xcMenuMapper;

    @Autowired
    ApplicationContext applicationContext;

    /**
     * @param s AuthParamsDto类型的json数据
     * @return org.springframework.security.core.userdetails.UserDetails
     * @description 查询用户信息组成用户身份信息
     * @author Mr.M
     * @date 2022/9/28 18:30
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        AuthParamsDto authParamsDto;
        try {
            //将接收到的认证参数转化为AuthParamsDto对象
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            log.info("认证请求不符合项目要求:{}", s);
            throw new RuntimeException("认证请求数据格式不对");
        }

        //
        AuthService authService = applicationContext.getBean(authParamsDto.getAuthType() + "_authservice", AuthService.class);
        XcUserExt xcUserExt = authService.execute(authParamsDto);

        //交由框架校验
        return getUserPrincipal(xcUserExt);
    }

    /**
     * @param xcUserExt 用户id，主键
     * @return com.xuecheng.ucenter.model.po.XcUser 用户信息
     * @description 查询用户信息
     * @author Mr.M
     * @date 2022/9/29 12:19
     */
    private UserDetails getUserPrincipal(XcUserExt xcUserExt) {

        //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
        String[] authorities = {""};

        //查询用户权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(xcUserExt.getId());

        //不为空，封装用户权限
        if (!CollectionUtils.isEmpty(xcMenus)) {
            List<String> permissionList = xcMenus.stream()
                    .map(XcMenu::getCode)
                    .collect(Collectors.toList());

            //设置到用户信息中
            xcUserExt.setPermissions(permissionList);

            //转成数组
            authorities = permissionList.toArray(new String[0]);
        }

        //获取正确密码
        String password = xcUserExt.getPassword();
        //安全起见将密码置空，不保存在令牌中
        xcUserExt.setPassword(null);

        //构建UserDetails对象
        String userString = JSON.toJSONString(xcUserExt);
        UserDetails userDetails = User.withUsername(userString).password(password).authorities(authorities).build();

        return userDetails;
    }
}
