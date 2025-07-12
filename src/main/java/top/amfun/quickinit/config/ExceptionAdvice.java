package top.amfun.quickinit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.amfun.quickinit.common.RestResponse;
import top.amfun.quickinit.common.exception.BusinessException;
import top.amfun.quickinit.common.exception.CommonErrorMessageEnum;
import top.amfun.quickinit.common.exception.SystemException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice("top.amfun")
public class ExceptionAdvice {

    /**
     * 自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public RestResponse handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("RemoteHost:{},uri:{},code:{},msg:{}", request.getRemoteHost(), request.getRequestURI(), e.getCode(), e.getMessage());
        return RestResponse.fail(e.getCode(), e.getMessage());
    }

    /**
     * 自定义系统异常
     */
    @ExceptionHandler(SystemException.class)
    @ResponseBody
    public RestResponse handleSystemException(SystemException e, HttpServletRequest request) {
        log.error("RemoteHost:{},uri:{},code:{},msg:{}", request.getRemoteHost(), request.getRequestURI(), e.getCode(), e.getMessage());
        return RestResponse.fail(e.getCode(), e.getMessage());
    }

    /**
     * RequestParam 参数校验
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public RestResponse missingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        SystemException ex = CommonErrorMessageEnum.DEFAULT.systemException("@RequestParam的参数校验失败: " + e.getMessage());
        log.error("RemoteHost:{},uri:{},msg:{}", request.getRemoteHost(), request.getRequestURI(), e.getMessage());
        return RestResponse.fail(ex.getCode(), ex.getMessage());
    }

    /**
     * validation 异常拦截
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public RestResponse validationException(ValidationException e, HttpServletRequest request) {
        SystemException ex = CommonErrorMessageEnum.DEFAULT.systemException("@RequestParam的参数校验失败: " + e.getMessage());
        log.error("RemoteHost:{},uri:{},msg:{}", request.getRemoteHost(), request.getRequestURI(), e.getMessage());
        return RestResponse.fail(ex.getCode(), ex.getMessage());
    }

    /**
     * 请求体读取异常（通常是请求参数格式非法、字段类型不匹配等）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public void handleJacksonException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.error("RemoteHost:{},uri:{}", request.getRemoteHost(), request.getRequestURI(), ex);
    }

    /**
     * 处理@Valid的参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public RestResponse<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        SystemException ex = CommonErrorMessageEnum.DEFAULT.systemException("参数校验失败: " + objectError.getDefaultMessage());
        return RestResponse.fail(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public RestResponse<String> bindExceptionHandler(BindException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        SystemException ex = CommonErrorMessageEnum.DEFAULT.systemException("参数校验失败: " + objectError.getDefaultMessage());
        return RestResponse.fail(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<RestResponse> handleException(Exception e, HttpServletRequest request) {
        if(e instanceof HttpRequestMethodNotSupportedException){
            return new ResponseEntity<>(RestResponse.fail(405, "method 方法不支持"), HttpStatus.METHOD_NOT_ALLOWED);
        } else if(e instanceof HttpMediaTypeNotSupportedException){
            return new ResponseEntity<>(RestResponse.fail(415, "不支持媒体类型"), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        } else if(e instanceof AccessDeniedException){
            return new ResponseEntity<>(RestResponse.fail(403, "无权限访问"), HttpStatus.FORBIDDEN);
        }
        log.error("RemoteHost:{},uri:{}", request.getRemoteHost(), request.getRequestURI(), e);
        BusinessException ex = CommonErrorMessageEnum.DEFAULT.unknownException();
        return new ResponseEntity<>(RestResponse.fail(ex.getCode(), ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
