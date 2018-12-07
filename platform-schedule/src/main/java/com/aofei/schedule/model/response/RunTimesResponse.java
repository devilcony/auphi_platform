package com.aofei.schedule.model.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RunTimesResponse {

    private List<String> names = new ArrayList<>();

    private List<Long> times = new ArrayList<>();
}
