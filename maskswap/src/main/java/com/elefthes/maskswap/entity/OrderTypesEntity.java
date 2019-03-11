package com.elefthes.maskswap.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "order_types")
public class OrderTypesEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "type_id")
    private int typeId;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
