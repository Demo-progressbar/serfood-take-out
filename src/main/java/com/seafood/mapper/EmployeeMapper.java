package com.seafood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seafood.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 *員工Mapper
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {


}
