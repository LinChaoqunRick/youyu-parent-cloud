package com.youyu.mapper;

import com.youyu.dto.DynamicListInput;
import com.youyu.entity.DynamicInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommonMapper {
    List<DynamicInfo> getUserDynamics(DynamicListInput input);
}
