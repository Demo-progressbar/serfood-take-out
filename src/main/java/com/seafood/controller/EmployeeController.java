package com.seafood.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seafood.common.Result;
import com.seafood.entity.Employee;

import com.seafood.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    /**
     * 員工登入方法
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request , @RequestBody Employee employee){

        //拿到密碼進行md5加密處理
        String password = employee.getPassword();
                    //md5加密工具DigestUtils.md5DigestAsHex
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //透過帳號查詢資料庫
        LambdaQueryWrapper<Employee> lambdaQueryWrapper =new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());

        Employee emp = employeeService.getOne(lambdaQueryWrapper);

        //查詢失敗直接返回結果
        if(emp == null){
            return Result.error("登入失敗");
        }
        //判斷密碼不正確
        if(!emp.getPassword().equals(password)){
            return Result.error("登入失敗");
        }
        //判斷當前帳號是否為禁用狀態
        if(emp.getStatus() == 0){
            return Result.error("此帳號為禁用狀態");
        }
        //登入成功, 把帳號信息存入session
        request.getSession().setAttribute("employee",emp.getId());

        return Result.success(emp);
    }

    /**
     * 員工登出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        //清除掉存在session中的員工ID
        request.getSession().removeAttribute("employee");

        return Result.success("登出成功");

    }

    /**
     * 新增員工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public Result<String> save(HttpServletRequest request,@RequestBody Employee employee){

        //log.info("成功訪問{}",employee);
        //設置密碼使用md5加密, 默認為123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //設置創建時間
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //設置創建人&更新人
        //Long empId = (Long)request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        //保存到資料庫
        employeeService.save(employee);

        return Result.success("新增員工成功");
    }

    /**
     * 分頁+條件查詢
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("page")
    public Result<Page> page (Long page,Long pageSize,String name){
        log.info("已接受到參數 page={}, pageSize={} ,name={}",page,pageSize,name);

        //創建Page物件
        Page pageInfo = new Page(page,pageSize);

        //使用MP的條件查詢設定條件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //判斷查詢條件是否為空+條件查詢
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //查詢數據
        employeeService.page(pageInfo,queryWrapper);

        return Result.success(pageInfo);
    }

    /**
     * 修改
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public Result<String> update (HttpServletRequest request,@RequestBody Employee employee){

        long threadId = Thread.currentThread().getId();
        log.info("當前執行緒ID= {}",threadId);

        log.info(employee.toString());
        //設定資料更新時間
        //employee.setUpdateTime(LocalDateTime.now());
        //設定修改人
        //Long empId = (Long)request.getSession().getAttribute("employee");
        //employee.setUpdateUser(empId);
        //調用service修改
        employeeService.updateById(employee);

        return Result.success("員工信息修改成功");

    }

    /**
     * 根據ID查詢員工訊息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){

        log.info("根據ID查詢員工訊息");

        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

}
