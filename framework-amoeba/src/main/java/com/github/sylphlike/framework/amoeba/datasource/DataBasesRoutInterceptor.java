package com.github.sylphlike.framework.amoeba.datasource;

import com.github.sylphlike.framework.adapt.UserAttributes;
import com.github.sylphlike.framework.adapt.UserContextHolder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Properties;

/**
 * 数据库路由拦截器
 * <p> 1 指定使用的数据库 ,2 字段自动填充 </p>
 * <p>  time 17:56 2019/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */


@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class }),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class })
})
@Configuration
public class DataBasesRoutInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBasesRoutInterceptor.class);

    /** 自动填充字段定义 */
    private static final String CREATE_USER = "createUser",CREATE_TIME = "createTime",UPDATE_USER = "updateUser",UPDATE_TIME = "updateTime";


    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object[] objects = invocation.getArgs();
        MappedStatement ms = (MappedStatement) objects[0];
        SqlCommandType sqlCommandType = ms.getSqlCommandType();

        boolean useMaster = DataSourceContextHolder.get().isForceUseMaster();
        LOGGER.debug("【FW-amoeba】强制路由到master数据库[{}]",useMaster);

        if(!useMaster && sqlCommandType.equals(SqlCommandType.SELECT)){
            LOGGER.debug("【FW-amoeba】 执行查询操作，使用slave数据库");
            DataSourceContextHolder.get().useDataBaseType(true,true);
        }else if(!useMaster){
            LOGGER.debug("【FW-amoeba】 执行CUD操作，使用master数据库");
            DataSourceContextHolder.get().useDataBaseType(false,true);
            autoFill(invocation,sqlCommandType);
        }else {
            autoFill(invocation,sqlCommandType);
        }
        return invocation.proceed();


    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }




    /**
     * 自动填充操作人属性字段
     * <p> create_user create_time update_user update_time 四个字段 </p>
     * <p>  time 18:18 2021/1/29      </p>
     * <p> email 15923508369@163.com  </p>
     * @param invocation     调用方式
     * @param sqlCommandType 语句类型
     * @throws  IllegalArgumentException ex
     * @author  Gopal.pan
     */
    private void autoFill(Invocation invocation, SqlCommandType sqlCommandType) throws IllegalAccessException {
        UserAttributes userAttributes = UserContextHolder.getRequestAttributes();
        if(StringUtils.isEmpty(userAttributes))
            return;

        //获取参数
        Object parameter = invocation.getArgs()[1];
        Field[] fields = parameter.getClass().getDeclaredFields();

        for(Field field: fields){
            //注入对应的属性值
            if (field.getName().equals(CREATE_TIME) && SqlCommandType.INSERT.equals(sqlCommandType)){
                field.setAccessible(true);
                field.set(parameter, LocalDateTime.now());
            }
            if (field.getName().equals(CREATE_USER) && SqlCommandType.INSERT.equals(sqlCommandType)){
                field.setAccessible(true);
                field.set(parameter, userAttributes.getUserId());
            }

            if (field.getName().equals(UPDATE_TIME) && SqlCommandType.UPDATE.equals(sqlCommandType)){
                field.setAccessible(true);
                field.set(parameter,LocalDateTime.now());
            }
            if (field.getName().equals(UPDATE_USER) && SqlCommandType.UPDATE.equals(sqlCommandType)){
                field.setAccessible(true);
                field.set(parameter, userAttributes.getUserId());
            }
        }
    }

}
