package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
    ApplicationContext applicationContext;

    /**
     * @description 查询用户信息组成用户身份信息
     * @param s  AuthParamsDto类型的json数据
     * @return org.springframework.security.core.userdetails.UserDetails
     * @author Mr.M
     * @date 2022/9/28 18:30
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        AuthParamsDto authParamsDto;
        try {
            //将接收到的认证参数转化为AuthParamsDto对象
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e){
            log.info("认证请求不符合项目要求:{}",s);
            throw new RuntimeException("认证请求数据格式不对");
        }

        //
        AuthService authService = applicationContext.getBean(authParamsDto.getAuthType() + "_authservice", AuthService.class);
        XcUserExt xcUserExt = authService.execute(authParamsDto);

        //交由框架校验
        return getUserPrincipal(xcUserExt);
    }

    /**
     * @description 查询用户信息
     * @param xcUserExt  用户id，主键
     * @return com.xuecheng.ucenter.model.po.XcUser 用户信息
     * @author Mr.M
     * @date 2022/9/29 12:19
     */
    private UserDetails getUserPrincipal(XcUserExt xcUserExt) {

        //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
        String[] authorities = {"p1"};
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
