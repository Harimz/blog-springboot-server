package com.spring.blog.services;

import com.spring.blog.domain.CreatePostRequest;
import com.spring.blog.domain.PostStatus;
import com.spring.blog.domain.UpdatePostRequest;
import com.spring.blog.domain.entities.Category;
import com.spring.blog.domain.entities.Post;
import com.spring.blog.domain.entities.Tag;
import com.spring.blog.domain.entities.User;
import com.spring.blog.domain.exceptions.posts.PostNotFoundException;
import com.spring.blog.repositories.PostRepository;
import com.spring.blog.services.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    PostRepository postRepository;

    @Mock
    CategoryService categoryService;

    @Mock
    TagService tagService;

    @Captor
    ArgumentCaptor<Post> postCaptor;

    PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostServiceImpl(postRepository, categoryService, tagService);
    }

    @Test
    void getPost_whenMissing_throwsPostNotFoundException() {
        UUID id = UUID.randomUUID();

        when(postRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPost(id))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining(id.toString());

        verify(postRepository).findById(id);
        verifyNoMoreInteractions(postRepository, categoryService, tagService);
    }

    @Test
    void createPost_savesPostWithCategoryTagsAndReadingTime() {
        User user = User.builder().build();

        UUID categoryId = UUID.randomUUID();
        UUID tagId1 = UUID.randomUUID();
        UUID tagId2 = UUID.randomUUID();

        CreatePostRequest req = CreatePostRequest.builder()
                .title("title")
                .content("one two three four five")
                .status(PostStatus.DRAFT)
                .categoryId(categoryId)
                .tagIds(Set.of(tagId1, tagId2))
                .build();

        Category category = Category.builder().id(categoryId).name("Tech").build();
        Tag tag1 = Tag.builder().id(tagId1).name("java").build();
        Tag tag2 = Tag.builder().id(tagId2).name("spring").build();

        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(tagService.getTagByIds(req.getTagIds())).thenReturn(List.of(tag1, tag2));

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post created = postService.createPost(user, req);

        assertThat(created.getTitle()).isEqualTo("title");
        assertThat(created.getContent()).isEqualTo(req.getContent());
        assertThat(created.getStatus()).isEqualTo(PostStatus.DRAFT);
        assertThat(created.getAuthor()).isSameAs(user);
        assertThat(created.getCategory()).isSameAs(category);
        assertThat(created.getTags()).extracting(Tag::getId)
                .containsExactlyInAnyOrder(tagId1, tagId2);
        assertThat(created.getReadingTime()).isEqualTo(1);

        verify(categoryService).getCategoryById(categoryId);
        verify(tagService).getTagByIds(req.getTagIds());
        verify(postRepository).save(any(Post.class));
    }
}
