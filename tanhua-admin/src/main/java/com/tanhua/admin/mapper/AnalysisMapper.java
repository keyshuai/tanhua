package com.tanhua.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.model.domain.Analysis;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisMapper extends BaseMapper<Analysis> {

    @Select("select sum(num_registered) from tb_analysis")
    Integer queryCumulativeUsers();
}
