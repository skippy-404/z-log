package com.zlog.admin.service;

import com.zlog.admin.entity.FilterByAI;
import com.zlog.admin.entity.UserInput;

public interface UserInputService {
    default FilterByAI filter(UserInput userInput){
        return new FilterByAI();
    };
}
