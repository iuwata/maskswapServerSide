package com.elefthes.maskswap.entity;

import com.elefthes.maskswap.entity.id.OrderVideoId;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "completed_videos")
@IdClass(OrderVideoId.class)
@NamedQuery(name = "CompletedVideoEntity.deleteByOrderId", query = "DELETE FROM CompletedVideosEntity c WHERE c.orderId = :orderId")
public class CompletedVideosEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "order_id")
    private long orderId;
    
    @Id
    @Column(name = "storage_order")
    private int storageOrder;
    
    @Column(name = "user_id")
    private long userId;
    
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

    public int getStorageOrder() {
        return storageOrder;
    }

    public void setStorageOrder(int storageOrder) {
        this.storageOrder = storageOrder;
    }
    
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
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
