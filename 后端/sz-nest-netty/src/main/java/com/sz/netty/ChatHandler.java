package com.sz.netty;

import com.sz.SpringUtil;
import com.sz.enums.MsgActionEnum;
import com.sz.pojo.Users;
import com.sz.service.UserService;
import com.sz.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * 处理消息的handler
 * TextWebSocketFrame：在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //用于记录和管理所有客户端的channel
    private static ChannelGroup users=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        //1. 获取客户端发来的消息
        String content = msg.text();
        Channel currentChannel = ctx.channel();
        DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
        Integer action = dataContent.getAction();

        // 2. 判断消息类型，根据不同的类型来处理不同的业务

        if (action == MsgActionEnum.CONNECT.type) {
            // 	2.1  当websocket 第一次open的时候，初始化channel，把用的channel和userid关联起来
            String senderId = dataContent.getChatMsg().getSenderId();
            UserChannelRel.deleteAll(senderId);
            UserChannelRel.put(senderId, currentChannel);
            UserChannelRel.putChannelIdAndUserId(currentChannel.id().asLongText(),senderId);
            //UserChannelRel.put("210227DAM2PTD9WA",currentChannel);

            // 测试
//            for (Channel c : users) {
//                System.out.println(c.id().asLongText());
//            }
            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            //String userid = UserChannelRel.getUserIdByChannelId(currentChannel.id().asLongText());
            //UserChannelRel.outputchannel_user();
            //UserChannelRel.output();
        }
        else if (action == MsgActionEnum.CHAT.type) {
            UserChannelRel.output();

            //  2.2  聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态[未签收]
            ChatMsg chatMsg = dataContent.getChatMsg();
            String msgText = chatMsg.getMsg();
            String receiverId = chatMsg.getReceiverId();
            String senderId = chatMsg.getSenderId();

            // 保存消息到数据库，并且标记为 未签收
            UserService userService = (UserService)SpringUtil.getBean("userServiceImpl");
            String msgId = userService.saveMsg(chatMsg);
            chatMsg.setMsgId(msgId);

            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setAction(MsgActionEnum.CHAT.type);
            dataContentMsg.setChatMsg(chatMsg);

            System.out.println("服务器接收到的消息"+chatMsg);

            // 发送消息
            // 从全局用户Channel关系中获取接受方的channel
            List<Channel> receiverChannel = UserChannelRel.get(receiverId);
            //System.out.println("当前接收方内的channel数量:"+receiverChannel.size());
            if (receiverChannel.get(0) == null) {
                // 说明用户离线，不用发消息
            } else {
                for (int i = 0; i < receiverChannel.size(); i++) {
                    // 当receiverChannel不为空的时候，从ChannelGroup去查找对应的channel是否存在
                    Channel findChannel = users.find(receiverChannel.get(i).id());
                    if (findChannel != null) {
                        // 用户在线
                        receiverChannel.get(i).writeAndFlush(
                                new TextWebSocketFrame(
                                        JsonUtils.objectToJson(dataContentMsg)));
                    } else {
                        // 用户离线
                        // receiverChannel存在但不代表ChannelGroup中该Channel仍然在线。有可能用户已经下线
                        // 所以，删除当前receivedId下的此channel
                        UserChannelRel.delete(receiverId,receiverChannel.get(i).id());
                        System.out.println("这是什么东西啊");
                        UserChannelRel.output();

                    }
                }
            }

        }
        else if (action == MsgActionEnum.SIGNED.type) {
            //  2.3  签收消息类型，针对具体的消息进行签收，修改数据库中对应消息的签收状态[已签收]
            UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
            // 扩展字段在signed类型的消息中，代表需要去签收的消息id，逗号间隔
            String msgIdsStr = dataContent.getExtand();
            String msgIds[] = msgIdsStr.split(",");

            List<String> msgIdList = new ArrayList<>();
            for (String mid : msgIds) {
                if (StringUtils.isNotBlank(mid)) {
                    msgIdList.add(mid);
                }
            }

            System.out.println(msgIdList.toString());

            if (msgIdList != null && !msgIdList.isEmpty() && msgIdList.size() > 0) {
                // 批量签收
                userService.updateMsgSigned(msgIdList);
            }

        }
        else if (action == MsgActionEnum.KEEPALIVE.type) {
            //  2.4  心跳类型的消息
            //System.out.println("收到来自channel为[" + currentChannel + "]的心跳包...");
        }
        else if (action == MsgActionEnum.SONG.type){
            // 将歌曲信息广播到该receivedId下的所有channel
            Song song = dataContent.getSong();
            System.out.println(song);
            String receiveId = song.getReceiveId();
            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setAction(MsgActionEnum.SONG.type);
            dataContentMsg.setSong(song);

            // 发送消息
            // 从全局用户Channel关系中获取接受方的channel
            List<Channel> receiverChannel = UserChannelRel.get(receiveId);
            //System.out.println("当前接收方内的channel数量:"+receiverChannel.size());
            if (receiverChannel.get(0) == null) {
                // 说明用户离线，不用发消息
            } else {
                for (int i = 0; i < receiverChannel.size(); i++) {
                    // 当receiverChannel不为空的时候，从ChannelGroup去查找对应的channel是否存在
                    Channel findChannel = users.find(receiverChannel.get(i).id());
                    if (findChannel != null ) {
                        // 用户在线
                        System.out.println("当前channel的ID:"+receiverChannel.get(i).id().asLongText());
                        receiverChannel.get(i).writeAndFlush(
                                new TextWebSocketFrame(
                                        JsonUtils.objectToJson(dataContentMsg)));
                    } else {
                        // 用户离线
                        // receiverChannel存在但不代表ChannelGroup中该Channel仍然在线。有可能用户已经下线
                        // 所以，删除当前receivedId下的此channel
                        UserChannelRel.delete(receiveId,receiverChannel.get(i).id());

                    }
                }

            }


        }
        else if (action == MsgActionEnum.ENTER.type){

            // 2.绑定roomId-channel 键值对
            String roomId = dataContent.getChatMsg().getReceiverId();
            System.out.println("用户（"+dataContent.getChatMsg().getSenderId()+"）加入房间（"+roomId+"）");

            UserChannelRel.put(roomId,currentChannel);
            UserChannelRel.output();


            // 1. 先向当前receivedid下的所有channel广播“我进入房间了”
            UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");

            ChatMsg chatMsg = dataContent.getChatMsg();
            String msgText = chatMsg.getMsg();

            String receiverId = chatMsg.getReceiverId();
            String senderId = chatMsg.getSenderId();

            // 保存消息到数据库，并且标记为 未签收
            chatMsg.setMsg("进入房间");
            String msgId = userService.saveMsg(chatMsg);
            chatMsg.setMsgId(msgId);

            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setAction(MsgActionEnum.ROMM_MSG.type);
            dataContentMsg.setChatMsg(chatMsg);


            System.out.println(chatMsg);

            // 发送消息
            // 从全局用户Channel关系中获取接受方的channel
            List<Channel> receiverChannel = UserChannelRel.get(receiverId);


            if (receiverChannel == null) {
                // TODO channel为空代表用户离线，推送消息（JPush，个推，小米推送）
            } else {

                for (int i = 0; i < receiverChannel.size(); i++) {
                    // 当receiverChannel不为空的时候，从ChannelGroup去查找对应的channel是否存在
                    Channel findChannel = users.find(receiverChannel.get(i).id());
                    if (findChannel != null) {
                        // 用户在线
                        receiverChannel.get(i).writeAndFlush(
                                new TextWebSocketFrame(
                                        JsonUtils.objectToJson(dataContentMsg)));
                    } else {
                        // 用户离线 TODO 推送消息
                        // receiverChannel存在但不代表ChannelGroup中该Channel仍然在线。有可能用户已经下线
                        // 所以，删除当前receivedId下的此channel
                        System.out.println("我没有删键值对吧");
                        UserChannelRel.delete(receiverId,receiverChannel.get(i).id());

                    }
                }
                System.out.println("难道被你删干净了？？");
                UserChannelRel.output();
            }




        }
        else if (action == MsgActionEnum.EXIT.type){
            System.out.println("用户离开房间");
            // 1. 先解除绑定roomId-channel 键值对
            String roomId = dataContent.getChatMsg().getReceiverId();
            UserChannelRel.delete(roomId,currentChannel.id());
            UserChannelRel.output();

            UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");


            // 2. 向当前receivedid下的所有channel广播“我退出房间了”
            ChatMsg chatMsg = dataContent.getChatMsg();
            chatMsg.setMsg("退出房间");
            String receiverId = chatMsg.getReceiverId();

            // 保存消息到数据库，并且标记为 未签收
            String msgId = userService.saveMsg(chatMsg);
            chatMsg.setMsgId(msgId);

            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setAction(MsgActionEnum.ROMM_MSG.type);
            dataContentMsg.setChatMsg(chatMsg);

            System.out.println(chatMsg);

            // 发送消息
            // 从全局用户Channel关系中获取接受方的channel
            List<Channel> receiverChannel = UserChannelRel.get(receiverId);

            if (receiverChannel == null) {
                // TODO channel为空代表用户离线，推送消息（JPush，个推，小米推送）
            } else {

                for (int i = 0; i < receiverChannel.size(); i++) {
                    // 当receiverChannel不为空的时候，从ChannelGroup去查找对应的channel是否存在
                    Channel findChannel = users.find(receiverChannel.get(i).id());
                    if (findChannel != null) {
                        // 用户在线
                        receiverChannel.get(i).writeAndFlush(
                                new TextWebSocketFrame(
                                        JsonUtils.objectToJson(dataContentMsg)));
                    } else {
                        // 用户离线 TODO 推送消息
                        // receiverChannel存在但不代表ChannelGroup中该Channel仍然在线。有可能用户已经下线
                        // 所以，删除当前receivedId下的此channel
                        UserChannelRel.delete(receiverId,receiverChannel.get(i).id());

                    }
                }
            }



        }
        else if (action == MsgActionEnum.ROMM_MSG.type){
            //  2.2  聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态[未签收]
            ChatMsg chatMsg = dataContent.getChatMsg();
            String msgText = chatMsg.getMsg();
            String receiverId = chatMsg.getReceiverId();
            String senderId = chatMsg.getSenderId();

            // 保存消息到数据库，并且标记为 未签收
            UserService userService = (UserService)SpringUtil.getBean("userServiceImpl");
            String msgId = userService.saveMsg(chatMsg);
            chatMsg.setMsgId(msgId);

            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setAction(MsgActionEnum.ROMM_MSG.type);
            dataContentMsg.setChatMsg(chatMsg);

            System.out.println("服务器接收到的消息"+chatMsg);

            // 发送消息
            // 从全局用户Channel关系中获取接受方的channel
            List<Channel> receiverChannel = UserChannelRel.get(receiverId);
            //System.out.println("当前接收方内的channel数量:"+receiverChannel.size());
            if (receiverChannel.get(0) == null) {
                // 说明用户离线，不用发消息
            } else {
                for (int i = 0; i < receiverChannel.size(); i++) {
                    // 当receiverChannel不为空的时候，从ChannelGroup去查找对应的channel是否存在
                    Channel findChannel = users.find(receiverChannel.get(i).id());
                    if (findChannel != null ) {
                        // 用户在线
                        System.out.println("当前channel的ID:"+receiverChannel.get(i).id().asLongText());
                        receiverChannel.get(i).writeAndFlush(
                                new TextWebSocketFrame(
                                        JsonUtils.objectToJson(dataContentMsg)));
                    } else {
                        // 用户离线
                        // receiverChannel存在但不代表ChannelGroup中该Channel仍然在线。有可能用户已经下线
                        // 所以，删除当前receivedId下的此channel
                        UserChannelRel.delete(receiverId,receiverChannel.get(i).id());
                        System.out.println("这是什么东西啊");


                    }
                }
                UserChannelRel.output();
            }
        }

    }

    /**
     * 当客户端连接服务器端之后（打开链接）
     * 获取客户端的channel，并且放到ChannelGroup中去进行管理
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
        System.out.println("新建连接"+ctx.channel().id().asShortText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //当触发handlerRemoverd，ChannelGroup会自动移除对应客户端的channel
        users.remove(ctx.channel());

        //得到userid
        String userId = UserChannelRel.getUserIdByChannelId(ctx.channel().id().asLongText());
        //删除userid对应的chuannellist
        UserChannelRel.deleteAll(userId);
        // 删除channelId-userId的绑定
        UserChannelRel.deleteChannelIdAndUserId(ctx.channel().id().asLongText());

        System.out.println("客户端断开，channel对应的长id为：" + ctx.channel().id().asLongText());
        System.out.println("客户端断开，channel对应的短id为：" + ctx.channel().id().asShortText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //发生异常之后关闭连接（关闭channel），随后从ChannelGroup中移除
        ctx.channel().close();
        users.remove(ctx.channel());
    }



}
