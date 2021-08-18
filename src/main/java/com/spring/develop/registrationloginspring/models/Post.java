package com.spring.develop.registrationloginspring.models;

import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "title", length = 1024)
    private String title;

    @Column(name = "fullDescription",length = 4096)
    private String fullDescription;

    @Column(name = "shortDescription", length = 2048)
    private String shortDescription;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "postDate")
//    @Type(type="date")
    private LocalDate postDate;

    @Column(name = "category")
    private String category;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinTable(
            name = "post_user",
            joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id",referencedColumnName = "id")
    )
    private Set<User> users;


    public Post() {
    }

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

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public LocalDate getPostDate() {
        return postDate;
    }

    public void setPostDate(LocalDate postDate) {
        this.postDate = postDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
