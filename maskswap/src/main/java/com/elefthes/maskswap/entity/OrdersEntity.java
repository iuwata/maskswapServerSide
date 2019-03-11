package com.elefthes.maskswap.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
@NamedQueries({
    @NamedQuery(name = "Orders.byId", query = "SELECT o FROM OrdersEntity o WHERE o.userId = :userId"),
    @NamedQuery(name = "Orders.orderById", query = "SELECT o FROM OrdersEntity o WHERE o.isStarting = 1 ORDER BY o.endDate asc, o.typeId desc, o.orderId asc")
})
public class OrdersEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private long orderId;
    
    //@ManyToOne
    //@JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @Column(name = "user_id")
    //private UsersEntity user;
    long userId;
    
    @Column(name = "type_id")
    int typeId;
    
    @Column(name = "progress")
    private int progress;
    
    @Column(name = "order_date")
    private Timestamp orderDate;
    
    @Column(name = "end_date")
    private Timestamp endDate;
    
    @Column(name = "is_converting")
    private boolean isConverting;
    
    @Column(name = "is_starting")
    private boolean isStarting;
    
    @Column(name = "dst_storage")
    private int dstStorage;
    
    @Column(name = "src_storage")
    private int srcStorage;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    /*public UsersEntity getUser() {
        return user;
    }

    public void setUser(UsersEntity user) {
        this.user = user;
    }*/

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getTypeId() {
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
    
    public boolean getIsStarting() {
        return isStarting;
    }

    public void setIsStarting(boolean isStarting) {
        this.isStarting = isStarting;
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
    
}
