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
        responseEntity.setCode(500);  // 使用500表示错误
        responseEntity.setMessage("error");
        return responseEntity;
    }

    /**
     * 返回带有错误消息的错误响应
     * @param errorMessage 错误消息
     * @return 响应实体
     */
    public static ResponseEntity error(String errorMessage)
    {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setCode(500);  // 使用500表示错误
        responseEntity.setMessage(errorMessage);
        return responseEntity;
    }
}
