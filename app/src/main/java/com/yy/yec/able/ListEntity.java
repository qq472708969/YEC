package com.yy.yec.able;

import com.yy.yec.entity.xml.Entity;

import java.io.Serializable;
import java.util.List;

public interface ListEntity<T extends Entity> extends Serializable {

    public List<T> getList();
}
