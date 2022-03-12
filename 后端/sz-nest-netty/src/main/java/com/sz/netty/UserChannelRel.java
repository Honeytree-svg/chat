package com.sz.netty;

import java.util.*;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

/**
 * @Description: 用户id和channel的关联关系处理
 */
public class UserChannelRel {

	// userId或roomId —— 链接的channel
	private static HashMap<String, List<Channel>> manager = new HashMap<String, List<Channel>>();
	// 绑定channel.id.asLongText()　—— userId
	private static HashMap<String,String> channel_user = new HashMap<String,String>();
	// 绑定 userId 和 roomId，说明该用户当前在哪个房间
	// 还未实装
	private static HashMap<String, String> user_room = new HashMap<>();

	public static void put(String senderId, Channel channel) {
		List<Channel> temp1 = get(senderId);
		if (temp1 == null){
			System.out.println("不会一直都是空吧");
			temp1 = new ArrayList<Channel>();
		}
		temp1.add(channel);
		manager.put(senderId, temp1);
	}

	public static List<Channel> get(String senderId) {
		for (HashMap.Entry<String, List<Channel>> entry : manager.entrySet()) {
			if (entry.getKey()!= senderId)continue;
			List<Channel> temp1=entry.getValue();
			return temp1;
		}
		return manager.get(senderId);
	}

	public static void deleteAll(String userId){
		manager.remove(userId);
		output();
	}

	public static void delete(String senderId, ChannelId channelId){
		List<Channel> channelList = get(senderId);
		System.out.println(channelId.asLongText());
		for (int i = 0; i < channelList.size(); i++) {
			System.out.println(channelList.get(i).id().asLongText());
			if (channelId.asLongText()== channelList.get(i).id().asLongText()){ channelList.remove(i);break;}
		}
		if (channelList.isEmpty()){ manager.remove(senderId); }else { manager.put(senderId,channelList); }
	}

	public static void putChannelIdAndUserId(String channelId, String userId){
		channel_user.put(channelId,userId);
	}

	public static void deleteChannelIdAndUserId(String channelId){
		channel_user.remove(channelId);
	}

	public static String getUserIdByChannelId(String channelId) {
		return channel_user.get(channelId);
	}
	
	public static void output() {
		System.out.println("所有的UserId或RoomId对应的channel的Id");
		for (HashMap.Entry<String, List<Channel>> entry : manager.entrySet()) {
			List<Channel> temp=entry.getValue();

			System.out.print("UserId: " + entry.getKey()
							+ ", ChannelId: ");
			for (int i = 0; i < temp.size(); i++) {
				System.out.print(temp.get(i).id().asLongText() + ",");
			}
			System.out.println("");
		}
	}

	public static void outputchannel_user() {
		for (HashMap.Entry<String, String> entry : channel_user.entrySet()) {
			String temp=entry.getValue();
			System.out.println("ChannelId: " + entry.getKey()
					+ ", UserId: " + entry.getValue());

		}
	}
}
