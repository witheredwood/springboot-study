package com.withered.config;

import org.apache.shiro.SecurityUtils;
import com.withered.pojo.Account;
import com.withered.service.AccountService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

// 自定义realm
public class UserRealm extends AuthorizingRealm {
    @Autowired
    AccountService accountService;

    // 授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("执行了 ==> 授权 doGetAuthorizationInfo");

//        // 方案一：这里所有的用户都有权限。正常在数据库中获取不同用户的权限
//        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        info.addStringPermission("user:add");
//        info.addStringPermission("user:update");

        // 方案二：从数据库中获取权限
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Subject subject = SecurityUtils.getSubject();  // 获取当前登录的这个对象
        Account currentAccount = (Account) subject.getPrincipal();  //拿到Account 对象
        // 设置当前用户的权限
        info.addStringPermission(currentAccount.getPerm());

        return info;
    }

    // 认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("执行了 ==> 认证 doGetAuthenticationInfo");
        // 方案一：固定用户名和密码
//        String name = "root";
//        String password = "123456";
//        UsernamePasswordToken userToken = (UsernamePasswordToken) token;
//        System.out.println("userToken.getUsername() ==> " + userToken.getUsername());
//        if (!userToken.getUsername().equals(name)) {
//            return null;  // 抛出异常
//        }
//        // 密码认证由shiro做
//        return new SimpleAuthenticationInfo("", password, "");

        // 方案二：数据库取用户名和密码
        UsernamePasswordToken userToken = (UsernamePasswordToken) token;
        // 连接真实数据库
        Account account = accountService.getAccountByName(userToken.getUsername());
        if (account == null) {  // 没有这个账号
            return null;  // 抛出异常
        }
        // 放到session中
        Subject currentSubject = SecurityUtils.getSubject();
        Session session = currentSubject.getSession();
        session.setAttribute( "1oginUser", account);


        // 密码认证由shiro做
        return new SimpleAuthenticationInfo(account, account.getPwd(), "");
    }
}
