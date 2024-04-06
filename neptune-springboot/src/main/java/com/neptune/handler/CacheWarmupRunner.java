package com.neptune.handler;

import com.neptune.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CacheWarmupRunner implements CommandLineRunner {

    @Autowired
    private TagService tagService;

    @Override
    public void run(String... args) throws Exception {
        tagService.updateTagCache();
        tagService.loadArticleCache();
    }
}
