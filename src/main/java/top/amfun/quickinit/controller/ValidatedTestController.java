package top.amfun.quickinit.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.amfun.quickinit.common.RestResponse;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 参数校验测试
 *
 * @author zhaoxg
 * @date 2025/5/9 17:56
 */
@RestController
@RequestMapping("/public")
@Validated
public class ValidatedTestController {

    @GetMapping("/test")
    RestResponse test(@RequestParam("articleId") @NotNull(message = "稿件 ID 不能为空")  Long articleId,
                      @RequestParam("tagId") @NotNull(message = "标签 ID 不能为空") @Min(value = 100, message = "标签 ID 需要大于 0") Long tagId) {
        return RestResponse.success();
    }
}
