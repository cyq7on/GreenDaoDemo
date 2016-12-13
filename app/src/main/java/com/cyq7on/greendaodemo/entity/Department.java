package com.cyq7on.greendaodemo.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by cyq7on on 2016/12/13.
 */
@Entity
public class Department {
    private Long id;
    private String name;
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 1940321194)
    public Department(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    @Generated(hash = 355406289)
    public Department() {
    }
}
