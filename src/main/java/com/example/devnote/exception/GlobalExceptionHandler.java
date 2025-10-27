package com.example.devnote.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理类（Global Exception Handler）
 *
 * 作用：
 *  - 捕获全局 Controller 层的异常
 *  - 显示统一的错误页面（error.html）
 *  - 避免用户看到堆栈信息，提升用户体验
 *
 * @ControllerAdvice 是 Spring 提供的全局异常管理机制
 * 能让我们集中处理所有控制器（Controller）的错误
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理 404 错误（找不到页面）
     * Spring Boot 3.x 中默认不再抛出 NoHandlerFoundException
     * 但我们可以在全局拦截到它并返回自定义错误页
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        model.addAttribute("errorCode", 404);
        model.addAttribute("errorMessage", "页面未找到：" + ex.getRequestURL());
        return "error"; // 对应 templates/error.html
    }

    /**
     * 处理所有其他异常（如500服务器错误）
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneralError(Exception ex, Model model) {
        model.addAttribute("errorCode", 500);
        model.addAttribute("errorMessage", "服务器内部错误：" + ex.getMessage());
        return "error";
    }
}
