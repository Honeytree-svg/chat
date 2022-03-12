package com.sz.controller;

import com.sz.enums.OperatorFriendRequestTypeEnum;
import com.sz.enums.SearchFriendsStatusEnum;
import com.sz.netty.UserChannelRel;
import com.sz.pojo.Users;
import com.sz.pojo.bo.UsersBO;
import com.sz.pojo.vo.MyFriendsVO;
import com.sz.pojo.vo.UsersVO;
import com.sz.service.UserService;
import com.sz.song.HttpURLConnectionUtil;
import com.sz.utils.FastDFSClient;
import com.sz.utils.FileUtils;
import com.sz.utils.MD5Utils;
import com.sz.utils.MyJSONResult;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("u")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FastDFSClient fastDFSClient;

    @PostMapping("/registOrLogin")
    public MyJSONResult Login(@RequestBody Users user)throws Exception{

        //0.判断用户名和密码不能为空
        if (StringUtils.isBlank(user.getUsername())||StringUtils.isBlank(user.getPassword())){
            return MyJSONResult.errorMsg("用户名或密码不能为空...");
        }

        //判断用户名是否存在，如果存在就登录，如果不存在则注册
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
        Users userResult = null;

        if(usernameIsExist){
            //1.1 登录
            userResult = userService.queryUserForLogin(user.getUsername(),
                    MD5Utils.getMD5Str(user.getPassword()));

            if (userResult == null) {
                return MyJSONResult.errorMsg("用户名或密码不正确...");
            }

        }else {
            //1.2注册
            user.setNickname(user.getUsername());
            user.setFaceImage("M00/00/00/wKgBRmBkO_6AJfo5AAEh7bON4RM671_80x80.png");
            user.setFaceImageBig("M00/00/00/wKgBRmBkO_6AJfo5AAEh7bON4RM671.png");
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            userResult = userService.saveUser(user);
        }
        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userResult, userVO);

        return MyJSONResult.ok(userVO);
    }

    /**
     * @Description: 上传用户头像
     */
    @PostMapping("/uploadFaceBase64")
    public MyJSONResult uploadFaceBase64(@RequestBody UsersBO userBO) throws Exception {

        // 获取前端传过来的base64字符串, 然后转换为文件对象再上传
        String base64Data = userBO.getFaceData();
        String userFacePath = "D:\\" + userBO.getUserId() + "userface64.png";
        FileUtils.base64ToFile(userFacePath, base64Data);


        // 上传文件到fastdfs
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
        String url = fastDFSClient.uploadBase64(faceFile);
        System.out.println(url);

        //删除缓存文件
//        File file = new File(userFacePath);
//        if (file.exists()){
//            file.delete();
//        }

//		"dhawuidhwaiuh3u89u98432.png"
//		"dhawuidhwaiuh3u89u98432_80x80.png"

        // 获取缩略图的url
        String thump = "_80x80.";
        String arr[] = url.split("\\.");
        String thumpImgUrl = arr[0] + thump + arr[1];

        // 更细用户头像
        Users user = new Users();
        user.setId(userBO.getUserId());
        user.setFaceImage(thumpImgUrl);
        user.setFaceImageBig(url);

        Users result = userService.updateUserInfo(user);

        return MyJSONResult.ok(result);
    }

    /**
     * @Description: 设置用户昵称
     */
    @PostMapping("/setNickname")
    public MyJSONResult setNickname(@RequestBody UsersBO userBO) throws Exception {

        Users user = new Users();
        user.setId(userBO.getUserId());
        user.setNickname(userBO.getNickname());

        Users result = userService.updateUserInfo(user);

        return MyJSONResult.ok(result);
    }


    /**
     * @Description: 搜索好友接口, 根据账号做匹配查询而不是模糊查询
     */
    @PostMapping("/search")
    public MyJSONResult searchUser(String myUserId, String friendUsername)
            throws Exception {

        // 0. 判断 myUserId friendUsername 不能为空
        if (StringUtils.isBlank(myUserId)
                || StringUtils.isBlank(friendUsername)) {
            return MyJSONResult.errorMsg("");
        }

        // 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);
        if (status == SearchFriendsStatusEnum.SUCCESS.status) {
            Users user = userService.queryUserInfoByUsername(friendUsername);
            UsersVO userVO = new UsersVO();
            BeanUtils.copyProperties(user, userVO);
            return MyJSONResult.ok(userVO);
        } else {
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return MyJSONResult.errorMsg(errorMsg);
        }
    }

    /**
     * @Description: 发送关注好友的请求
     */
    @PostMapping("/addFriendRequest")
    public MyJSONResult addFriendRequest(String myUserId, String friendUsername)
            throws Exception {

        // 0. 判断 myUserId friendUsername 不能为空
        if (StringUtils.isBlank(myUserId)
                || StringUtils.isBlank(friendUsername)) {
            return MyJSONResult.errorMsg("");
        }

        // 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);
        if (status == SearchFriendsStatusEnum.SUCCESS.status) {
            userService.sendFriendRequest(myUserId, friendUsername);

            //这边需要写一个直接把好友插入数据库的，《非常重要》
            Users users=userService.queryUserInfoByUsername(friendUsername);
            //我 →关注→ 他。  所以我是他的粉丝，他是我的关注
            userService.saveFriends(myUserId,users.getId());
            //《非常重要》
            //使用websocket主动推送消息到请求发起者，更新他的粉丝列表为最新
            //数据库查询，更新我的关注列表为最新
            List<MyFriendsVO> mySubscribe = userService.queryMyFriends(myUserId);

            return MyJSONResult.ok(mySubscribe);

        } else {
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return MyJSONResult.errorMsg(errorMsg);
        }

    }


    /**
     * @Description: 关注的请求
     */
    @PostMapping("/queryFriendRequests")
    public MyJSONResult queryFriendRequests(String userId) {

        // 0. 判断不能为空
        if (StringUtils.isBlank(userId)) {
            return MyJSONResult.errorMsg("");
        }

        // 1. 查询用户接受到的朋友申请
        return MyJSONResult.ok(userService.queryFriendRequestList(userId));
    }

    /**
     * @Description: 取消关注
     */
    @PostMapping("/cancelSubscribe")
    public MyJSONResult cancelSubscribe(String myUserId,String friendUserId) {

        // 0. 删除数据库friend表中的朋友关系
        userService.deleteSubscribe(myUserId,friendUserId);
        // 1. 返回最新的关注列表
        return MyJSONResult.ok(userService.queryMyFriends(myUserId));
    }



    /**
     * @Description: 接受方 通过或者忽略朋友请求
     */
    @PostMapping("/operFriendRequest")
    public MyJSONResult operFriendRequest(String acceptUserId, String sendUserId,
                                             Integer operType) {

        // 0. acceptUserId sendUserId operType 判断不能为空
        if (StringUtils.isBlank(acceptUserId)
                || StringUtils.isBlank(sendUserId)
                || operType == null) {
            return MyJSONResult.errorMsg("");
        }

        // 1. 如果operType 没有对应的枚举值，则直接抛出空错误信息
        if (StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))) {
            return MyJSONResult.errorMsg("");
        }

        if (operType == OperatorFriendRequestTypeEnum.IGNORE.type) {
            // 2. 判断如果忽略好友请求，则直接删除好友请求的数据库表记录
            userService.deleteFriendRequest(sendUserId, acceptUserId);
        } else if (operType == OperatorFriendRequestTypeEnum.PASS.type) {
            // 3. 判断如果是通过好友请求，则互相增加好友记录到数据库对应的表
            //	   然后删除好友请求的数据库表记录
            userService.deleteFriendRequest(sendUserId, acceptUserId);
        }

        // 4. 数据库查询好友列表
        List<MyFriendsVO> myFirends = userService.queryMyFriends(acceptUserId);

        return MyJSONResult.ok(myFirends);
    }

    /**
     * @Description: 查询我的关注的请求
     */
    @PostMapping("/queryMySubscribe")
    public MyJSONResult queryMySubscribe(String userId) {

        // 0. 判断不能为空
        if (StringUtils.isBlank(userId)) {
            return MyJSONResult.errorMsg("");
        }

        // 1. 查询用户接受到的朋友申请
        return MyJSONResult.ok(userService.queryMyFriends(userId));
    }


    /**
     * @Description: 查询我的粉丝的请求
     */
    @PostMapping("/queryMyFan")
    public MyJSONResult queryMyFan(String userId) {

        // 0. 判断不能为空
        if (StringUtils.isBlank(userId)) {
            return MyJSONResult.errorMsg("");
        }

        // 1. 查询用户接受到的朋友申请
        return MyJSONResult.ok(userService.queryMyFans(userId));
    }


    /**
     * @Description: 搜索歌曲列表
     */
    @PostMapping("/searchSong")
    public MyJSONResult searchSong(String data) throws Exception {

        String str1 = URLEncoder.encode( data, "UTF-8" );
        String str = "http://music.163.com/api/search/get/web?csrf_token=hlpretag=&hlposttag=&s=" + str1 + "&type=1&offset=0&total=true&limit=20";
        String message = HttpURLConnectionUtil.doGet(str);

        return MyJSONResult.ok(message);
    }

    /**
     * @Description: 搜索歌曲
     */
    @PostMapping("/searchSongOnlyOne")
    public MyJSONResult searchSongOnlyOne(String songId) throws Exception {

        String message = HttpURLConnectionUtil.doGet("https://api.imjad.cn/cloudmusic/?type=song&id=" + songId);

        return MyJSONResult.ok(message);
    }

    /**
     * @Description: 设置用户昵称
     */
    @PostMapping("/getUserById")
    public MyJSONResult getUserById(String userId) throws Exception {

        Users users = userService.queryUserInfoById(userId);
        return MyJSONResult.ok(users);
    }

    /**
     *
     * @Description: 用户手机端获取未签收的消息列表
     */
    @PostMapping("/getUnReadMsgList")
    public MyJSONResult getUnReadMsgList(String acceptUserId) {
        // 0. userId 判断不能为空
        if (StringUtils.isBlank(acceptUserId)) {
            return MyJSONResult.errorMsg("");
        }

        // 查询列表
        List<com.sz.pojo.ChatMsg> unreadMsgList = userService.getUnReadMsgList(acceptUserId);

        return MyJSONResult.ok(unreadMsgList);
    }



    /**
     * @Description: 设置用户昵称
     */
    @PostMapping("/test")
    public void test() throws Exception {
        List<Channel> channels = UserChannelRel.get("210330BR73MF3TR4");
        Channel channel = channels.get(0);
        System.out.println(channel.id().asLongText());
        UserChannelRel.output();
        UserChannelRel.delete("210330BR73MF3TR4",channel.id());
        UserChannelRel.output();
        System.out.println("无了");
        if (UserChannelRel.get("210330BR73MF3TR4")!=null){
            channels = UserChannelRel.get("210330BR73MF3TR4");
            System.out.println("啊啊啊啊啊啊啊啊啊啊啊啊啊");
        }else {
            System.out.println("噢噢噢噢噢噢噢噢噢噢噢噢哦哦哦");
        }
    }
}
