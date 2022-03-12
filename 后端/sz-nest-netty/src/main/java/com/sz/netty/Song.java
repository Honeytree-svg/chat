package com.sz.netty;



public class Song{

    private String userId;

    private String receiveId;

    private String songId;

    private String url;

    private String songName;

    private String singer;

    private String cover;

    private String duration;

    public String getUserId() {
        return userId;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSongId() {
        return songId;
    }

    public void setSngId(String singId) {
        this.songId = singId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Song{" +
                "userId='" + userId + '\'' +
                ", receiveId='" + receiveId + '\'' +
                ", songId='" + songId + '\'' +
                ", url='" + url + '\'' +
                ", songName='" + songName + '\'' +
                ", singer='" + singer + '\'' +
                ", cover='" + cover + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}
