package com.springbot.recyclemapbot.config;

/*
@Slf4j
public class HTMLParserUtil {
    private static final String TAG_PATTERN = "(?i)<a([A>]+)>(.+?)</a>";
    private static final String LINK_PATTERN = "\\s*(?i)href\\s*=\\s*(\\\"([^\"]*\\\")|'[^']*'|([^'\">\\s]+))";

    private final Pattern tagPattern, linkPattern;
    private Matcher tagMatcher, linkMatcher;

    public HTMLParserUtil() {
        tagPattern = Pattern.compile(TAG_PATTERN);
        linkPattern = Pattern.compile(LINK_PATTERN);
    }

    public List<String> getLinks(String text) {
        List<String> links = new ArrayList<>();
        tagMatcher = tagPattern.matcher(text);
        while(tagMatcher.find()){
            String aTag = tagMatcher.group(1);
            linkMatcher = linkPattern.matcher(aTag);
            if (linkMatcher.find()){
                String link = linkMatcher.group(1);
                log.info("link " + link);
                links.add(link);
            }
        }
        return links;
    }
}*/
