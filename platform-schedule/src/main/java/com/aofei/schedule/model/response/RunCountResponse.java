package com.aofei.schedule.model.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RunCountResponse {

    private List<String> datetimes  = new ArrayList<>();

    private List<String> errors  = new ArrayList<>();

    private List<String> finishs  = new ArrayList<>();
}
