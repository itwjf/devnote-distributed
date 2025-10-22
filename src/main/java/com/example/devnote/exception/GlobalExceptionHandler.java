package com.example.devnote.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 全局异常处理类
 * @ControllerAdvice 用于全局异常处理
 * 这个类会捕获整个应用中的异常，并统一处理。
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理所有未捕获的异常（如 NullPointerException 等）
     * @param e 异常信息
     * @param model 用于向视图传递数据
     * @return 错误页面视图名称
     * @ResponseStatus：用于指定返回的 HTTP 状态码，例如 404 或 500，这对错误响应很有帮助。
     * @ExceptionHandler：用于捕获指定类型的异常，比如 Exception.class 或自定义异常。
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error";  // 返回 error.html 页面
    }

    /**
     * 处理资源未找到异常（例如 404）
     * 这里是演示自定义异常的处理
     * @param e 自定义异常
     * @param model 用于传递数据到视图
     * @return 错误页面视图
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error";  // 返回 error.html 页面
    }

    // 可以添加更多的异常处理
}
