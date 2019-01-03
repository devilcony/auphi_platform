package com.aofei.schedule.model.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RunTimesResponse {

    private List<Object> names = new ArrayList<>();

    private List<Object> times = new ArrayList<>();
}
