package top.amfun.quickinit.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonErrorMessageEnum  implements AbstractErrorMessageEnum {
    DEFAULT(-1,"出错了"),
    UN_IMPLEMENT(-2,"未实现"),
    ILLEGAL_ARGUMENT(100000,"错误参数"),
    NULL_VALUE(100001,"不允许的空值"),
    NO_PERMISSION(100002,"无权访问他人数据"),
    NO_LOGIN(100003,"未登录或者token无效"),
    ILLEGAL_ARGUMENT_FORMAT(100004,"错误参数格式"),
    SERVICE_UNAVAILABLE(100005,"服务维护中，请稍后再试"),
    DATA_CONFLICT(100006,"数据已存在"),
    TRANSACTION_FAIL(100007,"暂时无法处理此操作"),
    UNKNOWN_SERVER_GROUP(100008,"你可能切换了host，重新登录试试"),
    ILLEGAL_DATA(100009,"数据逻辑错误"),
    NO_URI_PERMISSION(100010,"没有这个功能权限,请联系管理员授权"),
    CIRCULAR_DEPENDENCE(100011,"资源被循环依赖，请检查数据有效性!"),
    NO_DATA_PERMISSION(100012,"还未分配权限此数据权限"),
    JSON_READ_EXCEPTION(100013,"对象转换为json字符串失败"),
    JSON_WRITE_EXCEPTION(100014,"json字符串转换为对象失败"),
    ;
    private Integer code;
    private String errorMessage;
}
