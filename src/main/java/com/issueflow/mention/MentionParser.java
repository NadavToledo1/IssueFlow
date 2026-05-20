package com.issueflow.mention;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class MentionParser {

    private static final Pattern PATTERN = Pattern.compile("@([A-Za-z0-9_\\.\\-]+)");

    public Set<String> extractUsernames(String content) {
        Matcher m = PATTERN.matcher(content);
        return m.results()
                .map(r -> r.group(1).toLowerCase())
                .collect(Collectors.toSet());
    }
}
