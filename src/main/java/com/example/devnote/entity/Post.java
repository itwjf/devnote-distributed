package com.example.devnote.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * @Entity 表示这个类是一个 JPA 实体，会映射到数据库的一张表
 * @Table(name = "posts") 指定数据库表名为 `posts`
 * 如果不写 @Table，默认表名是类名小写：post
 */
@Entity
@Table(name = "posts")
public class Post {
    /**
     * @Id 表示这是主键
     * 数据库中会作为唯一标识，比如 1, 2, 3...
     * @GeneratedValue(strategy = GenerationType.IDENTITY)
     * 表示主键由数据库自动增长（auto_increment）
     * 比如 MySQL 的 AUTO_INCREMENT
     * 插入数据时，不用手动设置 id，数据库会自动分配
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column(nullable = false)
     * 表示这是一个数据库字段，且不允许为 NULL
     * 生成的 SQL 会是：title VARCHAR(255) NOT NULL
     */
    @Column(nullable = false)
    private String title;

    /**
     * @Lob 表示“大对象”（Large Object）
     * 适合存储长文本（如文章内容）
     * columnDefinition = "TEXT" 指定数据库类型为 TEXT（而不是默认的 VARCHAR(255)）
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 文章创建时间
     * updatable = false 表示更新时不会修改这个字段
     * 比如执行 update 时，createdAt 不会被更新
     */
    @Column(updatable = false)
    private LocalDateTime createdAt;


    public Post() {
        // JPA 要求实体类必须有一个无参构造函数
    }

    // ================== Getter 和 Setter ==================
    // 注意：JPA 通过 setter 方法设置字段值，不能删！
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


}
