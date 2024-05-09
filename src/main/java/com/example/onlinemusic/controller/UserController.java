package com.example.onlinemusic.controller;

import com.example.onlinemusic.mapper.UserMapper;
import com.example.onlinemusic.model.User;
import com.example.onlinemusic.tools.Constant;
import com.example.onlinemusic.tools.ResponseBodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private  BCryptPasswordEncoder bCryptPasswordEncoder; //加密

    @RequestMapping("/login1")
    public ResponseBodyMessage<User> login1(@RequestParam String username,@RequestParam String password,
            HttpServletRequest request){

        User userLogin = new User();
        userLogin.setUsername(username);
        userLogin.setPassword(password);

       User user = userMapper.login(userLogin);
       if(user==null){
           System.out.println("登陆失败");
          return  new ResponseBodyMessage<>(-1,"登陆失败",userLogin);

       }else{
           request.getSession().setAttribute(Constant.USERINFO_SESSION_KRY,user);
           System.out.println("登陆成功");
           return  new ResponseBodyMessage<>(0,"登陆成功",userLogin);
       }
    }


    @RequestMapping("/login")
    public ResponseBodyMessage<User> login(@RequestParam String username,@RequestParam String password,
                                           HttpServletRequest request){

//        User userLogin = new User();
//        userLogin.setUsername(username);
//        userLogin.setPassword(password);

   //     User user = userMapper.login(userLogin);
        User user = userMapper.selectByname(username);

        if(user==null){
            System.out.println("登陆失败");
            return  new ResponseBodyMessage<>(-1,"登陆失败",user);

        }else{
            //加密判断方法
            boolean flag = bCryptPasswordEncoder.matches(password,user.getPassword());
                if(!flag){
                    return new ResponseBodyMessage<>(-1,"用户名或密码错误",user);
                }
           HttpSession httpSession = request.getSession();
         httpSession.setAttribute(Constant.USERINFO_SESSION_KRY,user);
         User user1 = (User) httpSession.getAttribute(Constant.USERINFO_SESSION_KRY);
            System.out.println( user1);
            System.out.println("登陆成功");
            return  new ResponseBodyMessage<>(0,"登陆成功",user);
        }
    }
}
