package com.example.devnote.controller;

import com.example.devnote.entity.Post;
import com.example.devnote.repository.PostRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Controller 表示这是一个 Spring MVC 控制器
 * 它会处理 HTTP 请求，并返回视图名称（如 HTML 页面）
 */
@Controller
public class BlogController {
    /**
     * 使用构造函数注入 PostRepository
     *
     * 为什么用构造函数注入？
     * - 更安全：避免 null 引用
     * - 更容易测试（比如单元测试可以传入 mock 对象）
     * - 是 Spring 官方推荐的方式
     */
    private final PostRepository postRepository;

    public BlogController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * @GetMapping("/") 处理 GET 请求，路径是根路径 /
     * 当用户访问 http://localhost:8080 时，执行这个方法
     *
     * Model 是 Spring 提供的对象，用于向页面传递数据
     */
    @GetMapping("/")
    public String index(Model model) {
        // 调用 Repository 查询所有文章
        List<Post> posts = postRepository.findAll();
        // 把数据放入 Model，键是 "posts"，值是文章列表
        model.addAttribute("posts",posts);
        // 返回视图名称：index.html
        // Spring 会自动去 templates/ 目录下找 index.html
        return "index";
    }

    /**
     * @GetMapping("/new") 处理 /new 的 GET 请求
     * 显示写新文章的表单页面
     */
    @GetMapping("/new")
    public String newPostForm(Model model) {
        // 创建一个空的 Post 对象，用于表单绑定
        model.addAttribute("post", new Post());
        return "new";
    }

    /**
     * @PostMapping("/save") 处理 /save 的 POST 请求
     * 当用户提交表单时，执行这个方法
     *
     * Spring MVC 会自动把表单字段（title、content）映射到 Post 对象的属性上
     * 这叫 “数据绑定”（Data Binding）
     */
    @PostMapping("/save")
    public String savePost(@Valid Post post, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 如果验证失败，返回新文章页面并显示错误信息
            return "new";
        }
        // 保存文章到数据库
        post.setCreatedAt(LocalDateTime.now());//设置创建时间
        postRepository.save(post);
        // 重定向到首页，防止重复提交
        return "redirect:/";
    }
}
