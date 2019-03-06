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
    @NamedQuery(name = "Orders.byId", query = "SELECT o From OrdersEntity o WHERE o.userId = :userId")
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
    
    @Column(name = "progress")
    private int progress;
    
    @Column(name = "order_date")
    private Timestamp orderDate;
    
    @Column(name = "end_date")
    private Timestamp endDate;
    
    @Column(name = "is_free")
    private boolean isFree;

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

    public boolean getIsFree() {
        return isFree;
    }
    
    public void setIsFree(boolean isFree) {
        this.isFree = isFree;
    }
}
