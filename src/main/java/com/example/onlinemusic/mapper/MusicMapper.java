package com.example.onlinemusic.mapper;

import com.example.onlinemusic.model.Music;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MusicMapper {

      //数据库添加音乐
      int  insert(String title,String singer,String time,String url,int userid);

      //查重音乐
      Music select(String title);

      //删除音乐
      int deleteMusicById(int musicId);

      //查询当前ID音乐是否存在
      Music findMusicById(int id);

      /**
       * 根据歌曲名字，查询音乐
       * @param name
       * @return
       */
      List<Music> findMusicByMusicName(String name);
      /**
       * 查询所有的音乐
       * @return
       */
      List<Music> findMusic();
      }
