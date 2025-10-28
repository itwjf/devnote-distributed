package com.example.devnote.controller;

import com.example.devnote.entity.Comment;
import com.example.devnote.entity.Post;
import com.example.devnote.entity.User;
import com.example.devnote.repository.CommentRepository;
import com.example.devnote.repository.PostRepository;
import com.example.devnote.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
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
    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    //用构造函数注入
    public BlogController(PostRepository postRepository, UserRepository userRepository,CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * @GetMapping("/") 处理 GET 请求，路径是根路径 /
     * 当用户访问 http://localhost:8080 时，执行这个方法
     *
     * Model 是 Spring 提供的对象，用于向页面传递数据
     */
    @GetMapping({"/","/posts"})
    public String index(Model model) {
        // 调用 Repository 查询所有文章
        //查询的时候按照时间倒序排列，使用户体验更好
        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC,"createdAt"));//使用了 Spring Data JPA 的排序功能，保持用户体验一致性
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
    @GetMapping("/posts/new")
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
    @PostMapping("/posts/save")
    public String savePost(@ModelAttribute Post post) {
        // 从 Spring Security 中获取当前登录的用户名
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 查询用户对象
        User currentUser = userRepository.findByUsername(username);
        // 设置作者
        post.setAuthor(currentUser);
// 保存到数据库
        postRepository.save(post);

        // 重定向到首页，防止重复提交
        return "redirect:/";
    }

    /**
     * 文章详情页
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id,Model model){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章未找到"));


        List<Comment> comments = commentRepository.findByPostAndParentIsNullOrderByCreatedAtAsc(post);


        model.addAttribute("post",post);
        model.addAttribute("comments",comments);

        return "post_detail";
    }

    /**
     * 文件编辑页面
     * @param id
     * @param model
     * @param principal
     * @return
     */
    @GetMapping("/posts/{id}/edit")
    public String editPOst(@PathVariable Long id, Model model, Principal principal){
        Post post = postRepository.findById(id)
                .orElseThrow(()->new RuntimeException("文章不存在"));

        if (!post.getAuthor().getUsername().equals(principal.getName())){
            throw new RuntimeException("你没有权限编辑这篇文章");
        }

        //将文章传递给模板
        model.addAttribute("post",post);
        return "edit_post";
    }

    /**
     *更新文章内容
     * @param id
     * @param updatedPost
     * @param principal
     * @return
     */
    @PostMapping("/posts/{id}/edit")
    public String updatePost(@PathVariable Long id,
                             @ModelAttribute Post updatedPost,
                             Principal principal){
        //查找原文章
        //查找数据库中的对象
        Post existingPost = postRepository.findById(id)
                .orElseThrow(()->new RuntimeException("文章不存在"));

        //权限校验
        if (!existingPost.getAuthor().getUsername().equals(principal.getName())){
            throw new RuntimeException("你没有权限编辑这篇文章");
        }

        //更新字段
        //修改数据库中的对象，而不是直接保存前端传来的对象，更安全
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());

        //保存修改
        //save方法特性：对象有ID就执行update，没有ID就执行insert
        postRepository.save(existingPost);

        //返回文章详情页
        return "redirect:/posts/" + id;
    }

    /**
     * 删除文章（仅作者本人可删除）
     */
    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        // 查找目标文章
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        // 权限校验：仅作者本人可删除
        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new RuntimeException("你没有权限删除这篇文章");
        }

        // 执行删除操作
        postRepository.delete(post);

        // 使用 RedirectAttributes 添加临时提示信息（Flash Attribute）
        redirectAttributes.addFlashAttribute("message", "文章《" + post.getTitle() + "》已删除");
        // 返回首页并提示
        return "redirect:/";
    }

}
