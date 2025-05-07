package com.zlog.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseEntity {
    private Integer code;
    private String message;
    private Object data;
    public static ResponseEntity success()
    {
        ResponseEntity responseEntity= new ResponseEntity();
        responseEntity.setCode(200);
        responseEntity.setMessage("success");
        return responseEntity;
    }
    public static ResponseEntity success(Object data)
    {
        return new ResponseEntity(200,"success",data);
    }
    public static ResponseEntity error()
    {
        ResponseEntity responseEntity= new ResponseEntity();
        responseEntity.setCode(200);
        responseEntity.setMessage("error");
        return responseEntity;
    }

}
