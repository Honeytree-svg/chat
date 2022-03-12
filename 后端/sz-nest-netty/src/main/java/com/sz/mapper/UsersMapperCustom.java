package com.sz.mapper;

import java.util.List;

import com.sz.pojo.Users;
import com.sz.pojo.vo.FriendRequestVO;
import com.sz.pojo.vo.MyFriendsVO;
import com.sz.utils.MyMapper;

public interface UsersMapperCustom extends MyMapper<Users> {
	
	public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);
	
	public List<MyFriendsVO> queryMyFriends(String userId);

	public List<MyFriendsVO> queryMyFans(String userId);
	
	public void batchUpdateMsgSigned(List<String> msgIdList);

	public Users selectOnlyOne(Users users);

	public List<Users> batchSelectUserById(List<String> userIdList);
	
}