package com.example.onlinemusic.mapper;

import com.example.onlinemusic.model.Music;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoveMusicMapper {
    /**
     * 检查是否已经收藏过该音乐
     * @param userId
     * @param musicId
     * @return
     */
    Music findLoveMusicByMusicIdAndUserId(int userId, int musicId);
    /**
     * 点赞/收藏音乐
     * @param userId
     * @param musicId
     * @return
     */
    boolean insertLoveMusic(int userId, int musicId);
    /**
     * 如果没有传入具体的歌曲名，显示当前用户收藏的所有音乐
     * @param userId
     * @return
     */
    List<Music> findLoveMusicByUserId(int userId);
    /**
     * 根据某个用户的ID和歌曲名称查询，某个用户收藏的音乐
     * @param musicName
     * @param userId
     * @return
     */
    List<Music> findLoveMusicBykeyAndUID(String musicName, int userId);

    //删除收藏的音乐,根据用户 删音乐
    int deleteLoveMusic(int userId,int musicId);

    /**
     * 当删除库中的音乐的时候，同步删除lovemusic中的数据
     * @param musicId
     * @return
     */
    int deleteLoveMusicById(int musicId);
}