package com.example.devnote.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 收藏者

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post; // 被收藏的文章

    public Favorite(User user, Post post) {
        this.user = user;
        this.post = post;
    }

}
