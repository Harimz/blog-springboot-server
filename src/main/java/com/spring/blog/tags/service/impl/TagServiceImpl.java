package com.spring.blog.tags.service.impl;

import com.spring.blog.tags.Tag;
import com.spring.blog.tags.exception.TagInUseException;
import com.spring.blog.tags.exception.TagNotFoundException;
import com.spring.blog.tags.exception.TagsNotFoundException;
import com.spring.blog.tags.persistence.TagRepository;
import com.spring.blog.tags.service.TagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> getTags() {
        return tagRepository.findAllWithPostCount();
    }

    @Override
    @Transactional
    public List<Tag> createTags(Set<String> tagNames) {
        // Normalize incoming names a bit (optional but nice)
        Set<String> requestedNames = tagNames.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        if (requestedNames.isEmpty()) {
            return Collections.emptyList();
        }


        List<Tag> existingTags = tagRepository.findByNameIn(requestedNames);
        Set<String> existingTagNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        List<Tag> newTags = requestedNames.stream()
                .filter(name -> !existingTagNames.contains(name))
                .map(name -> Tag.builder()
                        .name(name)
                        .posts(new HashSet<>())
                        .build()
                )
                .toList();

        List<Tag> savedNewTags = newTags.isEmpty()
                ? Collections.emptyList()
                : tagRepository.saveAll(newTags);

        List<Tag> result = new ArrayList<>(existingTags.size() + savedNewTags.size());
        result.addAll(existingTags);
        result.addAll(savedNewTags);

        return result;
    }


    @Override
    @Transactional
    public void deleteTag(UUID id) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new TagNotFoundException(id));

        if (!tag.getPosts().isEmpty()) {
            throw new TagInUseException(id);
        }

        tagRepository.delete(tag);
    }

    @Override
    public Tag getTagById(UUID id) {
        return tagRepository.findById(id).orElseThrow(() -> new TagNotFoundException(id));
    }

    @Override
    public List<Tag> getTagByIds(Set<UUID> ids) {
        List<Tag> foundTags = tagRepository.findAllById(ids);
        if (foundTags.size() != ids.size()) {
            throw new TagsNotFoundException();
        }
        return foundTags;
    }
}
