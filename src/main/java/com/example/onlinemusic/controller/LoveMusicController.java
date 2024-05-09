package com.example.onlinemusic.controller;

import com.example.onlinemusic.mapper.LoveMusicMapper;
import com.example.onlinemusic.model.Music;
import com.example.onlinemusic.model.User;
import com.example.onlinemusic.tools.Constant;
import com.example.onlinemusic.tools.ResponseBodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/lovemusic")
public class LoveMusicController {

    //属性注入@Autowired
    @Resource
    private LoveMusicMapper loveMusicMapper;

    @RequestMapping("/likeMusic")
    public ResponseBodyMessage<Boolean> likeMusic(@RequestParam String id, HttpServletRequest request) {

        int musicId = Integer.parseInt(id);
        System.out.println("likeMusic->musicID: " + musicId);

        //检查登录
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USERINFO_SESSION_KRY) == null) {
            System.out.println("登陆失败");
            return new ResponseBodyMessage<>(-1, "登陆失败", false);

        }
        User user = (User) session.getAttribute(Constant.USERINFO_SESSION_KRY);
        int userId = user.getId();
        System.out.println("likeMusic->userID: " + userId);

        Music music = loveMusicMapper.findLoveMusicByMusicIdAndUserId(userId, musicId);


        if (music != null) {
            //说明收藏的音乐已经收藏,删除收藏的音乐

            return new ResponseBodyMessage<>(-1, "该用户点赞过该音乐", false);
        } else {
            boolean effect = loveMusicMapper.insertLoveMusic(userId, musicId);
            if (effect) {
                return new ResponseBodyMessage<>(0, "点赞音乐成功", true);
            } else {
                return new ResponseBodyMessage<>(-1, "点赞音乐失败", false);
            }
        }
    }

    @RequestMapping("/findlovemusic")
    public ResponseBodyMessage<List<Music>> findLoveMusic(@RequestParam(required = false) String musicName,
                                                          HttpServletRequest request) {
        //检查登录
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USERINFO_SESSION_KRY) == null) {
            System.out.println("登陆失败");
            return new ResponseBodyMessage<>(-1, "登陆失败,期待能够登陆后查找", null);

        }
        User user = (User) session.getAttribute(Constant.USERINFO_SESSION_KRY);
        int userId = user.getId();
        System.out.println("likeMusic->userID: " + userId);

        List<Music> musiclist = null;
        if (musicName == null) {
            musiclist = loveMusicMapper.findLoveMusicByUserId(userId);
        } else {
            musiclist = loveMusicMapper.findLoveMusicBykeyAndUID(musicName, userId);
        }
        return new ResponseBodyMessage<>(0, "查询到了所有歌曲信息", musiclist);

    }

    @RequestMapping("/deletelovemusic")

    public ResponseBodyMessage<Boolean> deleteLoveMusic(@RequestParam String id, HttpServletRequest request) {

        int musicId = Integer.parseInt(id);
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USERINFO_SESSION_KRY) == null) {
            System.out.println("登陆失败");
            return new ResponseBodyMessage<>(-1, "登陆失败,期待能够登陆后删除", null);

        }
        User user = (User) session.getAttribute(Constant.USERINFO_SESSION_KRY);
        int userId = user.getId();
        System.out.println("likeMusic->userID: " + userId);

        int ret = loveMusicMapper.deleteLoveMusic(userId,musicId);

        if(ret ==1 ){
            return  new ResponseBodyMessage<>(0, "取消收藏成功", true);

        }else
            return new ResponseBodyMessage<>(-1, "取消收藏失败", false);


    }

}