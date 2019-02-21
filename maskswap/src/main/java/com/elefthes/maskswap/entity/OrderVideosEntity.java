package com.elefthes.maskswap.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "order_videos")
public class OrderVideosEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private long orderId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UsersEntity user;
    
    @Column(name = "video")
    private byte[] video;
    
    @Column(name = "size")
    private int size;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public UsersEntity getUser() {
        return user;
    }

    public void setUser(UsersEntity user) {
        this.user = user;
    }

    public byte[] getVideo() {
        return video;
    }

    public void setVideo(byte[] video) {
        this.video = video;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
