package com.compass.ux.netty_lib.zhang;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author zhangchuang
 * @Description
 * @date 2020/6/15 12:31 下午
 **/
public class JacksonDatabind {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static ObjectMapper getInstance() {
    return MAPPER;
  }
}
