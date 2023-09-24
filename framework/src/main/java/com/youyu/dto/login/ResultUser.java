package com.youyu.dto.login;

import com.youyu.entity.UserFramework;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ResultUser {
    private UserFramework userInfo;
    private String token;
}
