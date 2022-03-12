package com.sz.service;

import com.sz.pojo.Room;
import com.sz.pojo.Users;
import org.springframework.stereotype.Service;

import java.util.List;


public interface RoomService {

    /**
     * @Description: 创建房间
     */
    public Room createRoom(Room room);

    /**
     * @Description: 创建房间
     */
    public List<Room> queryAllRoom();

    /**
     * @Description: 创建房间
     */
    public Room queryRoomById(String roomId);

    /**
     * @Description: 修改房间信息
     */
    public void updateRoomInfo(Room room);

    /**
     * @Description: 删除房间
     */
    public void deleteRoom(String roomId);


    /**
     * @Description: 查询userid的列表
     */
    public List<Users> queryUserList(List<String> userIdList);




}
