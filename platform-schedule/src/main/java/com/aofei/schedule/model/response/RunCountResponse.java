package com.aofei.schedule.model.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RunCountResponse {

    private List<Object> datetimes  = new ArrayList<>();

    private List<Object> errors  = new ArrayList<>();

    private List<Object> finishs  = new ArrayList<>();
}
