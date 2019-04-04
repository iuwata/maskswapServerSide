package com.elefthes.maskswap.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
@NamedQueries({
    @NamedQuery(name = "Orders.endDateNull", query = "SELECT o FROM OrdersEntity o WHERE o.userId = :userId AND o.endDate IS NULL"),
    @NamedQuery(name = "Orders.byUserId", query = "SELECT o FROM OrdersEntity o WHERE o.userId = :userId"),
    @NamedQuery(name = "Orders.orderById", query = "SELECT o FROM OrdersEntity o WHERE o.paymentDate IS NOT NULL AND o.isConverting = 0 AND o.typeId = :typeId ORDER BY o.paymentDate asc"),
    @NamedQuery(name = "Orders.byOrderId", query = "SELECT o FROM OrdersEntity o WHERE o.orderId = :orderId")
})
public class OrdersEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private long orderId;
    
    @Column(name = "user_id")
    private long userId;
    
    @Column(name = "type_id")
    private Integer typeId;
    
    @Column(name = "progress")
    private int progress;
    
    @Column(name = "order_date")
    private Timestamp orderDate;
    
    @Column(name = "end_date")
    private Timestamp endDate;
    
    @Column(name = "is_converting")
    private boolean isConverting;
    
    @Column(name = "payment_date")
    private Timestamp paymentDate;
    
    @Column(name = "dst_storage")
    private int dstStorage;
    
    @Column(name = "src_storage")
    private int srcStorage;
    
    @Column(name = "completed_storage")
    private int completedStorage;
    
    @Column(name = "src_duration")
    private Integer srcDuration;
    
    @Column(name = "dst_duration")
    private Integer dstDuration;
    
    @Column(name = "src_face_storage")
    private int srcFaceStorage;
    
    @Column(name = "dst_face_storage")
    private int dstFaceStorage;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public boolean getIsConverting() {
        return isConverting;
    }
    
    public void setIsConverting(boolean isConverting) {
        this.isConverting = isConverting;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public int getDstStorage() {
        return dstStorage;
    }

    public void setDstStorage(int dstStorage) {
        this.dstStorage = dstStorage;
    }

    public int getSrcStorage() {
        return srcStorage;
    }

    public void setSrcStorage(int srcStorage) {
        this.srcStorage = srcStorage;
    }

    public int getCompletedStorage() {
        return completedStorage;
    }

    public void setCompletedStorage(int completedStorage) {
        this.completedStorage = completedStorage;
    }

    public Integer getSrcDuration() {
        return srcDuration;
    }

    public void setSrcDuration(int srcDuration) {
        this.srcDuration = srcDuration;
    }

    public Integer getDstDuration() {
        return dstDuration;
    }

    public void setDstDuration(int dstDuration) {
        this.dstDuration = dstDuration;
    }

    public int getSrcFaceStorage() {
        return srcFaceStorage;
    }

    public void setSrcFaceStorage(int srcFaceStorage) {
        this.srcFaceStorage = srcFaceStorage;
    }

    public int getDstFaceStorage() {
        return dstFaceStorage;
    }

    public void setDstFaceStorage(int dstFaceStorage) {
        this.dstFaceStorage = dstFaceStorage;
    }
}
