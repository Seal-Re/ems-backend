package com.seal.subscribe.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubscribeDto {

    private List<String> eventTypes;

    private String eventDest;

    private int subType;

    private List<Integer> eventLvl;
}
