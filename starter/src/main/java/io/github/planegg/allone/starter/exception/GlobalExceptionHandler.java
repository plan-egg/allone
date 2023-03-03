package io.github.planegg.allone.starter.exception;


import io.github.planegg.allone.starter.common.util.IdGeneratorUtil;
import io.github.planegg.allone.starter.service.result.IReqResultFactory;
import io.github.planegg.allone.starter.service.result.ReqResultDti;
import io.github.planegg.allone.starter.service.result.ResultMsgKeyAlloneE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private IReqResultFactory reqResultFactory;

    /**
     * 捕获参数异常处理
     *
     * @param e
     * @param model
     * @return String
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ReqResultDti<?, ResultMsgKeyAlloneE> handelMissingServletRequestParameterException(MissingServletRequestParameterException e, Model model) {
        logger.error(ResultMsgKeyAlloneE.MISS_ARG.getMsg(), e);
        return reqResultFactory.createFailMsg( ResultMsgKeyAlloneE.MISS_ARG,e.getMessage());
    }

    /**
     * spring validator 方法参数验证异常拦截
     *
     * @param e 绑定验证异常
     * @return 错误返回消息
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ReqResultDti<?, ResultMsgKeyAlloneE> defaultErrorHandler(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String message = violation.getMessage();
        logger.info(ResultMsgKeyAlloneE.DATA_CHECK.getMsg(), message);
        return reqResultFactory.createFailMsg( ResultMsgKeyAlloneE.DATA_CHECK , message);
    }

    /**
     * 拦截实体类参数(参数前没有@RequestBody注解)校验失败的错误
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ReqResultDti<?, ResultMsgKeyAlloneE> bindExceptionHandler(BindException e) {
        ObjectError objectError = e.getAllErrors().get(0);
        logger.info(ResultMsgKeyAlloneE.DATA_BIND.getMsg(), objectError.getDefaultMessage());
        return reqResultFactory.createFailMsg(ResultMsgKeyAlloneE.DATA_BIND, objectError.getDefaultMessage());
    }

    /**
     * 拦截实体类参数(参数前有@RequestBody注解)校验失败的错误
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ReqResultDti <?, ResultMsgKeyAlloneE> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        logger.error("数据验证异常：{}",e.getMessage());
        List<ObjectError> objectErrorList = e.getBindingResult().getAllErrors();
        StringBuilder errMsgSb = new StringBuilder();
        for (ObjectError objectError : objectErrorList){
            errMsgSb.append(objectError.getDefaultMessage());
            errMsgSb.append(";  ");
        }
        return reqResultFactory.createFailMsg(errMsgSb.toString() );
    }


    /**
     * 处理业务处理异常
     * @param ex
     * @return
     */
    @ExceptionHandler(BizHandleException.class)
    public ReqResultDti handleBizHandleException(BizHandleException ex) {
        String errMsgId = IdGeneratorUtil.getUuidS();
        logger.error("异常捕获(报障编号={}): ",errMsgId, ex);
        return reqResultFactory.createFailMsg(ex.getMessage());
    }
    /**
     * 处理Exception
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ReqResultDti handleException(Exception ex) {
        String errMsgId = IdGeneratorUtil.getUuidS();
        logger.error("异常捕获(报障编号={}): {}",errMsgId, ex);
        Throwable cause = ex.getCause();
        String msg = "[报障编号："+errMsgId+"]";
        if (cause != null) {
            msg = msg+ cause.getMessage();
        } else {
            msg = msg + ex.getMessage();
        }
        return reqResultFactory.createFailMsg(msg);
    }
}
