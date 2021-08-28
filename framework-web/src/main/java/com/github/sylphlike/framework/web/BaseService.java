package com.github.sylphlike.framework.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sylphlike.framework.adapt.Constants;
import com.github.sylphlike.framework.norm.Response;
import com.github.sylphlike.framework.web.config.callback.AbstractTransaction;
import com.github.sylphlike.framework.web.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * <p>  time 17:56 2018/06/29  星期五 </p>
 * <p> email 15923508369@163.com     </p>
 * @author Gopal.pan
 * @version 1.0.0
 */

public class BaseService {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected ObjectMapper mapper;
    protected TransactionTemplate transactionTemplate;


    public BaseService(ObjectMapper mapper, TransactionTemplate transactionTemplate) {
        this.mapper = mapper;
        this.transactionTemplate = transactionTemplate;
    }

    /**
     * TransactionTemplate 编程式事物基类
     * 统一事物执行基类,解决大事物代码，拆分大事物，对业务层方法需要事物支持的代码进行精准控制
     * 为所有需要事物执行的方法提供通用的事物执行器，具体业务代码无需关注事物开启、异常回滚，提交
     * 默认使用PROPAGATION_REQUIRED 传播行为。
     * 当事物嵌套时如果是最内层事物代码块异常，最外层事物块可以不用去显示处理内层事物块返回异常，根据事务传播行为那么最外层回跟着回滚（Transaction rolled back because it has been marked as rollback-only）,本规则
     * 返回的错误码为 {"code":99999,"message":"未知异常 Transaction rolled back because it has been marked as rollback-only","data":null,"timestamp":"2020-06-17 09:19:26:671"}
     * 当事物嵌套时如果是最内层事物代码块异常，最外层事物块显示校验了内层事物的返回结果那么返回显示指定的异常码和描述信息
     *
     * 示例:
     *  return withTransaction(new AbstractTransaction<Void>() {
     *             @Override
     *             public Response<Void> execute() throws Exception {
     *                  //具体业务逻辑
     *             }
     *         }.logTitle("测试事物").logMethod("testTransaction").logArgs(new Response<>()).recordTransactionContentLog());
     *
     * @param abstractTransaction {@link AbstractTransaction}   手动事物回调执行方法抽象类
     * @return Response {@link Response} 事物执行后的结果参数，
     */
    protected <T> Response<T> withTransaction(final AbstractTransaction<T> abstractTransaction){

        return transactionTemplate.execute(transactionStatus -> {
            try {
                Response<T> exec = abstractTransaction.execute();
                transactionStatus.flush();
                return exec;
            }catch (DuplicateKeyException e){
                transactionStatus.setRollbackOnly();
                LOGGER.error("【FW-web】主键重复,事物回滚操作", e);
                return Response.error(FReply.FW_DATE_DUPLICATE_KEY);

            }catch (ServiceException e){
                transactionStatus.setRollbackOnly();
                LOGGER.error("【FW-web】业务异常,事物回滚操作", e);
                return  Response.error(e);

            }catch (Exception e) {
                transactionStatus.setRollbackOnly();
                LOGGER.error("【FW-web】系统异常", e);
                return new Response<>(FReply.FW_DATA_TRANSACTION);
            }
        });
    }



    /**
     * 校验数据库插入单挑记录是否成功
     * @param effectRow  数据库影响行数
     * @return  Response<java.lang.Void>
     * @author  Gopal.pan
     */
    protected Response<Void> checkAddData(int effectRow){
        LOGGER.info("【FW-web】校验数据库单条数据入库,影响行数[{}]",effectRow);
        if(effectRow == Constants.DIGITAL_ONE)
            return new Response<>();
        return new Response<>(FReply.FW_DATA_UNAFFECTED_ADD);

    }

    /**
     * 校验数据库修改一条记录是否成功
     * @param effectRow  数据库影响行数
     * @return  Response<java.lang.Void>
     * @author  Gopal.pan
     */
    protected Response<Void> checkUpdateData(int effectRow){
        LOGGER.info("【FW-web】校验数据库单条数据修改,影响行数[{}]",effectRow);
        if(effectRow == Constants.DIGITAL_ONE)
            return new Response<>();
        return new Response<>(FReply.FW_DATA_UNAFFECTED_UPDATE);
    }


    protected Response<Void> checkDeleteData(int effectRow){
        LOGGER.info("【FW-web】校验数据库单条数据删除,影响行数[{}]",effectRow);
        if(effectRow == Constants.DIGITAL_ONE)
            return Response.success();
        return new Response<>(FReply.FW_DATA_UNAFFECTED_DELETE);
    }



}
