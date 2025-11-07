package com.seal.subscribe.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("subscribe")
public class Subscribe implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("event_types")
    private String eventTypes;

    @TableField("event_dest")
    private String eventDest;

    @TableField("sub_type")
    private int subType;

    @TableField("event_lvl")
    private int eventLvl;
}
