package com.sz.pojo;

import javax.persistence.Column;
import javax.persistence.Id;

public class Room {
    @Id
    @Column(name = "room_id")
    private String roomId;

    @Column(name = "founder_id")
    private String founderId;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "room_image")
    private String roomImage;

    @Column(name = "room_image_big")
    private String roomImageBig;

    /**
     * @return room_id
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * @param roomId
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /**
     * @return founder_id
     */
    public String getFounderId() {
        return founderId;
    }

    /**
     * @param founderId
     */
    public void setFounderId(String founderId) {
        this.founderId = founderId;
    }

    /**
     * @return room_name
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * @param roomName
     */
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    /**
     * @return room_image
     */
    public String getRoomImage() {
        return roomImage;
    }

    /**
     * @param roomImage
     */
    public void setRoomImage(String roomImage) {
        this.roomImage = roomImage;
    }

    /**
     * @return room_image_big
     */
    public String getRoomImageBig() {
        return roomImageBig;
    }

    /**
     * @param roomImageBig
     */
    public void setRoomImageBig(String roomImageBig) {
        this.roomImageBig = roomImageBig;
    }
}