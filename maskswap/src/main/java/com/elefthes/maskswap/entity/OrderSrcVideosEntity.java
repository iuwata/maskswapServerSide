package com.elefthes.maskswap.entity;

import com.elefthes.maskswap.entity.id.OrderVideoId;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "order_src_videos")
@IdClass(OrderVideoId.class)
public class OrderSrcVideosEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "order_id")
    private long orderId;
    
    @Id
    @Column(name = "storage_order")
    private int storageOrder;
    
    //@ManyToOne
    //@JoinColumn(name = "user_id", referencedColumnName = "user_id")
    //private UsersEntity user;
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

/*    public UsersEntity getUser() {
        return user;
    }

    public void setUser(UsersEntity user) {
        this.user = user;
    }
*/
    
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
