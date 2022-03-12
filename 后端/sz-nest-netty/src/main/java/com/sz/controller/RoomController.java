package com.sz.controller;


import com.sz.mapper.RoomMapper;
import com.sz.netty.UserChannelRel;
import com.sz.pojo.Room;
import com.sz.pojo.bo.RoomBO;
import com.sz.service.RoomService;
import com.sz.utils.FastDFSClient;
import com.sz.utils.FileUtils;
import com.sz.utils.MyJSONResult;
import io.netty.channel.Channel;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("r")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private Sid sid;

    /**
     * @Description: 新建房间
     */
    @PostMapping("/createRoom")
    public MyJSONResult createRoom(@RequestBody Room room){
        Room result = roomService.createRoom(room);
        return MyJSONResult.ok(result);
    }

    /**
     * @Description: 新建房间
     */
    @PostMapping("/updateRoom")
    public MyJSONResult updateRoom(@RequestBody Room room){
        roomService.updateRoomInfo(room);
        return MyJSONResult.ok(room);
    }

    /**
     * @Description: 获得房间列表
     */
    @PostMapping("/getRoomList")
    public MyJSONResult getRoomList(){
        List<Room> result = roomService.queryAllRoom();
        return MyJSONResult.ok(result);
    }

    /**
     * @Description: 获得房间列表
     */
    @PostMapping("/getRoomById")
    public MyJSONResult getRoomById(String roomId){
        Room result = roomService.queryRoomById(roomId);
        return MyJSONResult.ok(result);
    }

    /**
     * @Description: 获得房间列表
     */
    @PostMapping("/deleteRoom")
    public MyJSONResult deleteRoom(String roomId){
        roomService.deleteRoom(roomId);
        UserChannelRel.deleteAll(roomId);
        return MyJSONResult.ok();
    }


    /**
     * @Description: 上传房间图片
     */
    @PostMapping("/uploadFaceBase64")
    public MyJSONResult uploadFaceBase64(@RequestBody RoomBO roomBO) throws Exception {

        // 获取前端传过来的base64字符串, 然后转换为文件对象再上传
        String base64Data = roomBO.getFaceData();
        String roomFacePath = "D:\\" + roomBO.getFounderId() + "roomface64.png";
        FileUtils.base64ToFile(roomFacePath, base64Data);

        // 上传文件到fastdfs
        MultipartFile faceFile = FileUtils.fileToMultipart(roomFacePath);
        String url = fastDFSClient.uploadBase64(faceFile);
        System.out.println(url);

        // 获取缩略图的url
        String thump = "_80x80.";
        String arr[] = url.split("\\.");
        String thumpImgUrl = arr[0] + thump + arr[1];

        // 更新房间信息
        Room room = new Room();
        room.setRoomId(sid.nextShort());
        room.setFounderId(roomBO.getFounderId());
        room.setRoomImage(thumpImgUrl);
        room.setRoomImageBig(url);
        room.setRoomName(roomBO.getRoomname());

        return MyJSONResult.ok(room);
    }


    /**
     * @Description: 获得房间用户列表
     */
    @PostMapping("/roomUserList")
    public MyJSONResult roomUserList(String roomId){
//        System.out.println("我开始喽：" + roomId);
//        UserChannelRel.output();
        List<Channel> userChannelList = UserChannelRel.get(roomId);
        if (userChannelList != null){
            //System.out.println("用户channel的数量：" + userChannelList.size());
            List<String> list = new ArrayList<>();
            for (int i = 0; i < userChannelList.size(); i++) {
                String str = UserChannelRel.getUserIdByChannelId(userChannelList.get(i).id().asLongText());
                //System.out.println("用户的id：" + str);
                list.add(str);
            }
            //System.out.println("用户列表的长度：" + list.size());
            return MyJSONResult.ok(roomService.queryUserList(list));
        }

        return MyJSONResult.ok();
    }

}
