package com.study.vip.mall.permission.init;

import com.study.vip.mall.model.Permission;
import com.study.vip.mall.permission.service.PermissionService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InitPermission implements ApplicationRunner {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;
    /**
     * 权限初始化加载
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //加载所有权限 0：完全匹配过滤 1:通配符匹配
        List<Permission> permissionMatch0 = permissionService.findByMatch(0);
        List<Permission> permissionMatch1 = permissionService.findByMatch(1);

        //所有角色的权限 查询角色权限映射关系
        List<Map<Integer, Integer>> rolePermissions = permissionService.allRolePermissions();
        //匹配每个角色拥有的权限列表
        Map<String, Set<Permission>> roleMap = rolePermissionFilter(rolePermissions, permissionMatch0, permissionMatch1);

        //数据存入到redis缓存
        redisTemplate.boundHashOps("RolePermissionAll").put("PermissionMatch0",permissionMatch0);
        redisTemplate.boundHashOps("RolePermissionAll").put("PermissionMatch1",permissionMatch1);
        //角色权限
        redisTemplate.boundHashOps("RolePermissionMap").putAll(roleMap);
        redisTemplate.boundHashOps("aa").put("bbbb","bbbbb");
        //uri初始化到布隆过滤器中
        RBloomFilter<String> filters = redissonClient.getBloomFilter("UriBloomFilterArray");
        //初始化数组长度以及误判概率
        filters.tryInit(10000L,0.001);
        //这里只演示完全匹配，通配符匹配还需要额外处理
        for (Permission permission : permissionMatch0) {
            filters.add(permission.getUrl());
        }
    }

    /**
     * 每个角色拥有的权限
     */
    public Map<String, Set<Permission>>  rolePermissionFilter(List<Map<Integer, Integer>> rolePermissions,
                                     List<Permission> permissionMatch0,
                                     List<Permission> permissionMatch1){
        //每个角色拥有哪些权限 存入Map中
        Map<String, Set<Permission>> rolePermissionMapping= new HashMap<String, Set<Permission>>();
        //循环所有的角色关系映射
        for (Map<Integer, Integer> rolePermissionMap : rolePermissions) {
            //角色ID
            Integer rid = rolePermissionMap.get("rid");
            //权限ID
            Integer pid = rolePermissionMap.get("pid");

            String key0 = "Role_0_"+rid.intValue();
            String key1 = "Role_1_"+rid.intValue();

            //获取当前角色拥有的权限
            Set<Permission> permissionSet0 = rolePermissionMapping.get(key0);
            Set<Permission> permissionSet1 = rolePermissionMapping.get(key1);
            //防止空指针
            permissionSet0=permissionSet0==null? new HashSet<Permission>(): permissionSet0;
            permissionSet1=permissionSet1==null? new HashSet<Permission>(): permissionSet1;

            //查找每个角色对应的权限
            //循环完全匹配路径
            for (Permission permission : permissionMatch0) {
                if(permission.getId().intValue()==pid.intValue()){
                    //权限匹配完成
                    permissionSet0.add(permission);
                    break;
                }
            }
            //循环通配符匹配路径
            for (Permission permission : permissionMatch1) {
                if(permission.getId().intValue()==pid.intValue()){
                    //权限匹配完成
                    permissionSet1.add(permission);
                    break;
                }
            }
            //将数据添加到rolePermissionMapping中
            if(permissionSet0.size()>0){
                rolePermissionMapping.put(key0,permissionSet0);
            }
            if(permissionSet1.size()>0){
                rolePermissionMapping.put(key1,permissionSet1);
            }
        }
        return rolePermissionMapping;
    }
}
