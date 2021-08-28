package com.github.sylphlike.framework.web.config.callback;

import com.github.sylphlike.framework.norm.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 手动事物回调执行方法抽象类
 * <p>  time 17:56 2018/06/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */
public abstract class AbstractTransaction<T>{

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransaction.class);

    public AbstractTransaction(){}

    /** 日志标题*/
    private String logTitle;

    /**执行方法*/
    private String logMethod;

    /** 参数*/
    private Object requestArgs;



    public void setLogTitle(String logTitle) {
        this.logTitle = logTitle;
    }
    public void setLogMethod(String logMethod) {
        this.logMethod = logMethod;
    }
    public void setRequestArgs(Object requestArgs) {
        this.requestArgs = requestArgs;
    }



    public AbstractTransaction<T> logTitle(String title) {
        setLogTitle(title);
        return this;
    }

    public AbstractTransaction<T> logMethod(String method) {
        setLogMethod(method);
        return this;
    }

    public AbstractTransaction<T> requestArgs(Object... obj) {
        setRequestArgs(obj);
        return this;
    }



    public AbstractTransaction<T> recordTransactionContentLog(){
        LOGGER.info("【FW-web】事物执行日志,logTitle[{}]; logMethod[{}]; requestArgs[{}]",logTitle,logMethod, requestArgs);
        return this;
    }




    /**
     *  事物执行抽象类，业务接口实现具体逻辑
     * @return   com.github.sylphlike.norm.Response
     * @author   Gopal.pan
     */
    public abstract Response<T> execute() throws  Exception;

}
