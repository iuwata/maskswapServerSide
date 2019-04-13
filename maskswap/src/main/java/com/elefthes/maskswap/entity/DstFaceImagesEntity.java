package com.elefthes.maskswap.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "dst_face_images")
@NamedQueries({
    @NamedQuery(name = "DstFaceImages.byId", query = "SELECT d FROM DstFaceImagesEntity d WHERE d.orderId = :orderId AND d.storageOrder = :storage"),
    @NamedQuery(name = "DstFaceImages.deleteById", query = "DELETE FROM DstFaceImagesEntity d WHERE d.orderId = :orderId")
})
public class DstFaceImagesEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "order_id")
    private long orderId;
    
    @Id
    @Column(name = "storage_order")
    private int storageOrder;
    
    @Column(name = "user_id")
    private long userId;
    
    @Column(name = "image")
    private byte[] image;
    
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
