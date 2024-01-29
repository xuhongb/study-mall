package com.study.vip.mall.api.permission;

import com.alibaba.fastjson.JSON;
import com.study.mall.util.JwtToken;
import com.study.mall.util.MD5;
import com.study.vip.mall.api.util.IpUtil;
import com.study.vip.mall.model.Permission;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AuthorizationInterceptor {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    /***
     * 无效路径过滤
     */
    public Boolean isInvalid(String uri){
        //获取数组对象
        RBloomFilter<Object> bloomFilterArray = redissonClient.getBloomFilter("UriBloomFilterArray");
        return bloomFilterArray.contains(uri);
    }
    /***
     * 角色权限校验
     */
    public Boolean rolePermission(ServerWebExchange exchange,Map<String, Object> token){
        //request
        ServerHttpRequest request = exchange.getRequest();
        //获取uri  /cart/list
        String uri = request.getURI().getPath();
        //提交方式  GET/POST/*
        String method = request.getMethodValue();
        //服务名字
        URI routerUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        String servicename = routerUri.getHost();

        //获取角色
        String[] roles = token.get("roles").toString().split(",");
        //当前角色权限
        Permission permission = null;

        //循环角色，获取角色权限
        for (String role : roles) {
            //===========完全匹配数据key0==============
            String key0 = "Role_0_"+role;
            //获取角色权限数据
            Set<Permission> rolePermissionList0 = (Set<Permission>) redisTemplate.boundHashOps("RolePermissionMap").get(key0);
            if(rolePermissionList0!=null){
                //匹配权限
                permission = match0(new ArrayList<>(rolePermissionList0), uri, method, servicename);
            }

            if(permission==null){
                String key1 = "Role_1_"+role;
                //===========通配符匹配数据key1，作业==============
                //获取角色权限数据
                Set<Permission> rolePermissionList1 = (Set<Permission>) redisTemplate.boundHashOps("RolePermissionMap").get(key1);
                if(rolePermissionList1!=null){
                    //通配符匹配权限
                    permission = match1(new ArrayList<>(rolePermissionList1), uri, method, servicename);
                }
            }

            //如果找不到权限，说明无权访问
            if(permission!=null){
                break;
            }
        }
        return permission!=null;
    }

    /***
     * 令牌校验
     */
    public Map<String, Object> tokenIntercept(ServerWebExchange exchange){
        //request
        ServerHttpRequest request = exchange.getRequest();
        //客户端IP
        String ip = IpUtil.getIp(request);
        //用户令牌
        String token = request.getHeaders().getFirst("authorization");
        //令牌校验
        Map<String, Object> resultMap = AuthorizationInterceptor.jwtVerify(token, ip);
        return resultMap;
    }

    /**
     * 是否需要拦截校验
     * true 需要拦截
     * false 不需要拦截
     */
    public boolean isIntercept(ServerWebExchange exchange){
        ServerHttpRequest request = exchange.getRequest();
        //获取uri /cart/list
        String uri = request.getURI().getPath();
        //提交方式 GET/POST/*
        String method = request.getMethodValue();
        //服务名字
        URI routerUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        String servicename = routerUri.getHost();
        //redisTemplate.boundHashOps("aa").put("aa","aaaaa");
        System.out.println(redisTemplate.boundHashOps("aa").get("aa"));
        //从Redis缓存中匹配
        // 0 完全匹配PermissionMatch0boundHashOps("RolePermissionAll").put("PermissionMatch0",
        List<Permission> permissionMatch0 = (List<Permission>) redisTemplate.boundHashOps("RolePermissionAll").get("PermissionMatch0");
        System.out.println(redisTemplate.boundHashOps("RolePermissionAll"));
        if(permissionMatch0 !=null){

        }
        System.out.println(permissionMatch0);
        Permission permission = match0(permissionMatch0, uri, method,servicename);
        // 1通配符匹配
        if(permission==null){
            //匹配通配符
            List<Permission> permissionMatch1 = (List<Permission>) redisTemplate.boundHashOps("RolePermissionAll").get("PermissionMatch1");
            //进行匹配，??
            permission = match1(permissionMatch1, uri, method,servicename);
        }
        //如果此时permission则表示不需要进行权限校验
        if(permission==null){
            //不需要权限校验
            return false;
        }

        return true;
    }

    /***
     * 匹配方法:完全匹配
     */
    public Permission match0(List<Permission> permissionsMatch0,String uri,String method,String serviceName){
        for (Permission permission : permissionsMatch0) {
            String matchUrl = permission.getUrl();
            String matchMethod = permission.getMethod();
            if(matchUrl.equals(uri)){
                //提交方式和服务匹配
                if(matchMethod.equals(method) && serviceName.equals(permission.getServiceName())){
                    return permission;
                }
                if("*".equals(matchMethod) && serviceName.equals(permission.getServiceName())){
                    return permission;
                }
            }
        }
        return null;
    }

    /***
     * 匹配方法:通配符匹配
     */
    public Permission match1(List<Permission> permissionsMatch0,String uri,String method,String serviceName){
        AntPathMatcher matcher = new AntPathMatcher();
        for (Permission permission : permissionsMatch0) {
            String matchUrl = permission.getUrl();
            String matchMethod = permission.getMethod();
            if(matcher.match(matchUrl,uri)){
                //提交方式和服务匹配
                if(matchMethod.equals(method) && serviceName.equals(permission.getServiceName())){
                    return permission;
                }
                if("*".equals(matchMethod) && serviceName.equals(permission.getServiceName())){
                    return permission;
                }
            }
        }
        return null;
    }

    /***
     * 令牌解析
     */
    public static Map<String, Object> jwtVerify(String token, String clientIp){
        try {
            //token解析
            Map<String, Object> resultMap = JwtToken.parseToken(token);
            //令牌中的IP
            String jwtip = resultMap.get("ip").toString();

            //IP校验
            clientIp = MD5.md5(clientIp);
            if(clientIp.equals(jwtip)){
                return resultMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
