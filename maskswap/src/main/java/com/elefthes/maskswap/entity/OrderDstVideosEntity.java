/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elefthes.maskswap.entity;

import com.elefthes.maskswap.entity.id.OrderVideoId;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "order_dst_videos")
@IdClass(OrderVideoId.class)
@NamedQueries({
    @NamedQuery(name = "OrderDstVideos.byId", query = "SELECT o FROM OrderDstVideosEntity o WHERE o.orderId = :orderId AND o.storageOrder = :storageOrder"),
    @NamedQuery(name = "OrderDstVideos.deleteByOrderId", query = "DELETE FROM OrderDstVideosEntity o WHERE o.orderId = :orderId")
})
public class OrderDstVideosEntity implements Serializable{
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
