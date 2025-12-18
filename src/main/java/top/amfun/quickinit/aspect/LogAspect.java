package top.amfun.quickinit.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import top.amfun.quickinit.annotation.LogRecord;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author zhaoxg
 * @date 2024/8/28 10:46
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* top.amfun.quickinit.service.*.*(..))")
    public void pointCut(){}

    @Before("pointCut()")
    public void beforeAdvice(JoinPoint joinPoint) {
        // 访问连接点信息，在目标方法执行前做处理（如日志、权限校验）
        System.out.println("Before method: " + joinPoint.getSignature().getName());
        Object[] args = joinPoint.getArgs(); // 获取方法参数
    }

    @After("pointCut()")
    public void afterAdvice(JoinPoint joinPoint) {
        // 无论方法成功与否，都会执行（如资源清理）
        System.out.println("After method: " + joinPoint.getSignature().getName());
        Object[] args = joinPoint.getArgs(); // 仍然可以访问参数
        // 无法在此直接获得方法返回值或异常
    }

    @AfterReturning(value = "@annotation(r)", returning = "result")
    public void log(JoinPoint joinPoint, Object result, LogRecord r) {
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            log.info("args[{}] = {}", i, args[i]);
            Object object = args[i];
            Class<?> aClass = object.getClass();
            Class<?> superclass = aClass.getSuperclass();
            Field[] declaredFieldsSuper = superclass.getDeclaredFields();
            Field[] declaredFields = aClass.getDeclaredFields();
            HashSet<Field> fields = new HashSet<>();
            fields.addAll(Arrays.asList(declaredFields));
            fields.addAll(Arrays.asList(declaredFieldsSuper));
            fields.forEach(field -> {
                try {
                    field.setAccessible(true);
                    if (field.get(object) != null) {
                        log.info("{} = {}", field.getName(), field.get(object));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        log.info("result = {}", result);
        log.info("type = {}, value = {}", r.type(), r.value());
    }

    @AfterThrowing(
            pointcut = "pointCut()",
            throwing = "ex" // 指定与异常对象的参数名对应
    )
    public void afterThrowingAdvice(JoinPoint joinPoint, Exception ex) {
        // ex参数接收目标方法抛出的异常
        System.out.println("Method " + joinPoint.getSignature().getName()
                + " threw exception: " + ex.getMessage());
        Object[] args = joinPoint.getArgs(); // 可以访问参数
        // 可以进行异常日志记录、转换或通知等操作
    }

    @Around("pointCut()")
    public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 方法执行之前的逻辑（例如：记录开始时间、验证/修改参数）
        System.out.println("Before proceeding method: " + proceedingJoinPoint.getSignature().getName());
        Object[] args = proceedingJoinPoint.getArgs(); // 可以访问并修改参数

        try {
            // 决定何时执行目标方法
            Object result = proceedingJoinPoint.proceed(); // 调用proceed()触发目标方法执行
            // proceed()可以传入修改后的参数数组：proceed(newArgs);

            // 方法正常返回后的逻辑（例如：记录结束时间、修改返回值）
            System.out.println("Method returned: " + result);
            // 可以修改返回值
            return result; // 返回给调用者的结果（可以是修改后的）
        } catch (Exception e) {
            // 方法抛出异常后的逻辑（例如：记录异常、抛出不同的异常）
            System.out.println("Method threw exception: " + e.getMessage());
            // 可以抛出原始异常、包装异常或新异常
            throw e; // 或者 throw new CustomException("Wrapped", e);
        } finally {
            // 无论成功失败都执行的逻辑（但通常在此定义的很少，建议用@After）
        }
    }

}
