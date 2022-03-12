package com.sz.service.impl;

import com.sz.mapper.RoomMapper;
import com.sz.mapper.RoomMapperCustom;
import com.sz.mapper.UsersMapperCustom;
import com.sz.pojo.FriendsRequest;
import com.sz.pojo.Room;
import com.sz.pojo.Users;
import com.sz.service.RoomService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private RoomMapperCustom roomMapperCustom;

    @Autowired
    private UsersMapperCustom usersMapperCustom;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Room createRoom(Room room) {
        String str = sid.nextShort();
        room.setRoomId(str);
        roomMapper.insert(room);
        return roomMapperCustom.selectOnlyOneByRoomId(str);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Room> queryAllRoom() {
        return roomMapperCustom.queryRoom();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Room queryRoomById(String roomId) {
        Room room = roomMapperCustom.selectOnlyOneByRoomId(roomId);
        return room;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateRoomInfo(Room room) {
        roomMapperCustom.updateRoom(room);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteRoom(String roomId) {
        Example fre = new Example(Room.class);
        Example.Criteria frc = fre.createCriteria();
        frc.andEqualTo("roomId", roomId);
        roomMapper.deleteByExample(fre);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Users> queryUserList(List<String> userIdList) {
        return usersMapperCustom.batchSelectUserById(userIdList);
    }
}
