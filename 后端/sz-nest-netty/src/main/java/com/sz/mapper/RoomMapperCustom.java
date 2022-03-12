package com.sz.mapper;

import com.sz.pojo.Room;
import com.sz.pojo.Users;
import com.sz.pojo.vo.FriendRequestVO;
import com.sz.pojo.vo.MyFriendsVO;
import com.sz.utils.MyMapper;

import java.util.List;

public interface RoomMapperCustom extends MyMapper<Users> {

	public Room selectOnlyOneByRoomId(String roomId);

	public Room selectOnlyOneByFounderId(String founderId);

	public List<Room> queryRoom();

	/**
	 * @Description: gengxin 房间
	 */
	public void updateRoom(Room room);
	
}