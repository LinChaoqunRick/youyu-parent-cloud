package com.youyu.dao;

import com.youyu.entity.UserRole;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * (SysUserRole)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-24 20:58:46
 */
public interface SysUserRoleMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param userId 主键
     * @return 实例对象
     */
    UserRole queryById(Long userId);

    /**
     * 查询指定行数据
     *
     * @param sysUserRole 查询条件
     * @param pageable         分页对象
     * @return 对象列表
     */
    List<UserRole> queryAllByLimit(UserRole sysUserRole, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param sysUserRole 查询条件
     * @return 总行数
     */
    long count(UserRole sysUserRole);

    /**
     * 新增数据
     *
     * @param sysUserRole 实例对象
     * @return 影响行数
     */
    int insert(UserRole sysUserRole);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<SysUserRole> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<UserRole> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<SysUserRole> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<UserRole> entities);

    /**
     * 修改数据
     *
     * @param sysUserRole 实例对象
     * @return 影响行数
     */
    int update(UserRole sysUserRole);

    /**
     * 通过主键删除数据
     *
     * @param userId 主键
     * @return 影响行数
     */
    int deleteById(Long userId);

}

