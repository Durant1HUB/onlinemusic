package com.example.onlinemusic.controller;

import com.example.onlinemusic.mapper.LoveMusicMapper;
import com.example.onlinemusic.mapper.MusicMapper;
import com.example.onlinemusic.model.Music;
import com.example.onlinemusic.model.User;
import com.example.onlinemusic.tools.Constant;
import com.example.onlinemusic.tools.ResponseBodyMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.naming.Binding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/music")
public class MusicController {
    @Value("${music.local.path}")
    private String SAVE_PATH;//  "D:/work/music/";

    @Autowired
    private MusicMapper musicMapper;

    @Resource
    private LoveMusicMapper lovemusicMapper;

    @RequestMapping("/upload")
    public ResponseBodyMessage<Boolean> insertMusic(@RequestParam String singer,
                                                    @RequestParam("filename") MultipartFile file,
                                                    HttpServletRequest req,HttpServletResponse resp) throws IOException {


        //MultipartFile  框架上传文件类型，接口
        //没有session不创建
        HttpSession httpSession = req.getSession(false);

        //session为空 或者属性为空
        if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KRY) == null) {
            System.out.println("登陆失败");
            return new ResponseBodyMessage<>(-1, "请正确登录后上传", false);
        }


//        先在数据库查询是否已经上传过这首歌，歌手，歌名
        String fileNameAndType1 = file.getOriginalFilename();
        int index1 = fileNameAndType1.lastIndexOf(".");
        String title1 = fileNameAndType1.substring(0,index1);
        Music music= musicMapper.select(title1);

        if( music!=null){
            return new ResponseBodyMessage<>(-1,"上传过了该音乐",false);
        }

        //没有重复歌名创建文件
        String fileNameAndType = file.getOriginalFilename();
        System.out.println("fileNameAndType" +"/"+ fileNameAndType);

        String path = SAVE_PATH +"/" + fileNameAndType;
        //读文件
        File dest = new File(path);
        if (!dest.exists()) {
            dest.mkdir();
        }
        //文件上传

        try {
            file.transferTo(dest);
    //        return new ResponseBodyMessage<>(0, "上传成功", true);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseBodyMessage<>(-1, "上传失败！", false);
        }
      //     return new ResponseBodyMessage<>(-1,"上传失败！",false);
//
//        进行数据库的上传
//        1.进行数据准备
//        2.调用insert()
      int index = fileNameAndType.lastIndexOf(".");
      String title = fileNameAndType.substring(0,index);

    User user = (User) httpSession.getAttribute(Constant.USERINFO_SESSION_KRY);
      int userid = user.getId();

      //1.播放音乐——》http请求
        String url = "/music/get?path="+title;

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sf.format(new Date());
        System.out.println(title+singer+time+url+userid);

        try {
            int ret = 0;
            ret = musicMapper.insert(title,singer,time,url,userid);
            if(ret == 1) {
                //这里应该跳转到音乐列表页面
                resp.sendRedirect("/list.html");
                return new ResponseBodyMessage<>(0,"数据库上传成功！",true);
            }else {
                return new ResponseBodyMessage<>(-1,"数据库上传失败！",false);
            }
        }catch (BindingException e) {
            dest.delete();
            return new ResponseBodyMessage<>(-3,"数据库上传失败！",false);
        }

        }

        //可以返回歌曲的字节，可以查是否是MP3 ,TAG字段
    @RequestMapping("/get")
    public ResponseEntity<byte[]> get(String path){
            File file = new File(SAVE_PATH + "/" + path);
            byte[] a =null;
        try {
            a = Files.readAllBytes(file.toPath());
            if(a != null) {
                return ResponseEntity.ok(a);
            }else{
                return ResponseEntity.badRequest().build();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }
//        @RequestMapping("/get")
//        public ResponseEntity<byte[]> func() {
//            byte[] a = {97,98,99,100};
//    //return ResponseEntity.internalServerError().build();
//            return ResponseEntity.notFound().build();
//    //return ResponseEntity.ok(a);
//            //返回字节，body内容
//        }


    //    删除音乐
    @RequestMapping("/delete")
    public ResponseBodyMessage<Boolean> deleteMusicById(@RequestParam String id) {
        //1.先检查该音乐是否存在
        int iid = Integer.parseInt(id);//字符串办变整数
        Music music = musicMapper.findMusicById(iid);
        if(music == null){
            System.out.println("没有音乐");
            return new ResponseBodyMessage<>(-1,"没有你要删除的音乐",false);
        }else{
        //2.若存在要删除（数据库，服务器上的数据）
            int ret = musicMapper.deleteMusicById(iid);
            if(ret == 1){
                //删除服务器上的数据
              int index =  music.getUrl().lastIndexOf("=");
                String filename = music.getUrl().substring(index+1);
                File file = new File(SAVE_PATH+"/"+filename+".mp3");
                System.out.println("当前路径"+file.getPath());
                if(!file.delete()){

                       return new ResponseBodyMessage<>(-1,"服务器删除音乐失败",false);
                }else{
                    //同步删除lovemusic的音乐
                    lovemusicMapper.deleteLoveMusicById(iid);
                    return new ResponseBodyMessage<>(0,"服务器删除音乐成功",true);
                }
            }else{
                return new ResponseBodyMessage<>(-1,"数据库删除音乐失败",false);
            }
        }
    }

    //删除多个音乐
    @RequestMapping("/deleteSel")
    public ResponseBodyMessage<Boolean> deleteSelMusic(@RequestParam("id[]") List<Integer> id) {
        System.out.println("所有的ID ： " + id);
        int sum = 0;
        for (int i = 0; i < id.size(); i++) {
            int musicId = id.get(i);
            Music music = musicMapper.findMusicById(musicId);
            if (music == null) {
                System.out.println("没有这个id的音乐");
                return new ResponseBodyMessage<>(-1, "没有你要删除的音乐", false);
            }
            int ret = musicMapper.deleteMusicById(musicId);
            if (ret == 1) {
                //2.2 删除服务器上的数据
                int index = music.getUrl().lastIndexOf("=");
                String fileName = music.getUrl().substring(index + 1);//liu

                File file = new File(SAVE_PATH + "/" + fileName + ".mp3");
                if (file.delete()) {

                    //同步删除lovemusic的音乐
                    lovemusicMapper.deleteLoveMusicById(musicId);
                    sum += ret;
                    //return new ResponseBodyMessage<>(0,"服务器当中的音乐删除成功！",true);
                } else {
                    return new ResponseBodyMessage<>(-1, "服务器当中的音乐删除失败！", false);
                }
            } else {
                return new ResponseBodyMessage<>(-1, "数据库当中的音乐删除失败！", false);
            }
        }
        if (sum == id.size()) {
            System.out.println("整体删除成功！");
            return new ResponseBodyMessage<>(0, "音乐删除成功！", true);
        } else {
            System.out.println("整体删除失败！");
            return new ResponseBodyMessage<>(-1, "音乐删除失败！", false);
        }
    }

    // 查找音乐模块设计，0-多个
    @RequestMapping("/findmusic")//(required=false)可以不传入参数
    public ResponseBodyMessage<List<Music>> findMusic(@RequestParam(required=false) String musicName) {

        List<Music> musicList = null;//列表存储
        if(musicName != null) {
            //模糊查询
            musicList = musicMapper.findMusicByMusicName(musicName);
        }else {
            //默认查询全部的音乐
            musicList = musicMapper.findMusic();
        }
        return new ResponseBodyMessage<>(0,"查询到了歌曲的信息",musicList);
    }
}


