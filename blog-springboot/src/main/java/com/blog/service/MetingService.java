package com.blog.service;

import com.blog.vo.MetingSongVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Set;

/**
 * 通过 Meting 兼容 API 解析各平台歌曲信息（网易云、QQ 音乐等）。
 * 详见 <a href="https://github.com/injahow/meting-api">meting-api</a>，需可访问的 API 基础地址。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetingService {

    private static final Set<String> ALLOWED_SERVERS = Set.of(
            "netease", "tencent", "kugou", "baidu", "kuwo", "xiami"
    );

    private static final String DEFAULT_METING_BASE = "https://api.injahow.cn/meting/";

    private final SysConfigService sysConfigService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();

    /**
     * @param server Meting server：netease、tencent（QQ）、kugou 等
     * @param id     平台歌曲 id
     */
    public MetingSongVO resolveSong(String server, String id) throws Exception {
        if (server == null || server.isBlank() || id == null || id.isBlank()) {
            throw new IllegalArgumentException("server 与 id 不能为空");
        }
        server = server.trim().toLowerCase();
        if (!ALLOWED_SERVERS.contains(server)) {
            throw new IllegalArgumentException("不支持的平台: " + server);
        }
        log.info("[Meting] 解析请求，server: {}，id: {}", server, id);
        String base = sysConfigService.getValue("meting_api_base");
        if (base == null || base.isBlank()) {
            base = DEFAULT_METING_BASE;
        }
        base = base.trim();
        if (!base.endsWith("/")) {
            base = base + "/";
        }
        String q = "server=" + URLEncoder.encode(server, StandardCharsets.UTF_8)
                + "&type=song&id=" + URLEncoder.encode(id.trim(), StandardCharsets.UTF_8);
        String requestUrl = base + "?" + q;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .timeout(Duration.ofSeconds(15))
                .header("User-Agent", "NeptuneBlog/1.0 (Meting)")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            log.warn("[Meting] HTTP {} url={}", response.statusCode(), requestUrl);
            throw new IllegalStateException("音乐 API 返回 " + response.statusCode());
        }
        String body = response.body();
        if (body == null || body.isBlank()) {
            throw new IllegalStateException("音乐 API 空响应");
        }

        JsonNode root = objectMapper.readTree(body);
        if (root.isObject() && root.has("error")) {
            throw new IllegalStateException(root.get("error").asText("unknown error"));
        }
        JsonNode first;
        if (root.isArray() && root.size() > 0) {
            first = root.get(0);
        } else if (root.isObject()) {
            first = root;
        } else {
            throw new IllegalStateException("无法解析歌曲数据");
        }
        if (first.has("error")) {
            throw new IllegalStateException(first.get("error").asText("unknown error"));
        }

        MetingSongVO vo = new MetingSongVO();
        vo.setName(first.path("name").asText(""));
        vo.setArtist(first.path("artist").asText(""));
        vo.setUrl(first.path("url").asText(""));
        vo.setPic(first.path("pic").asText(""));
        if (vo.getUrl() == null || vo.getUrl().isBlank()) {
            throw new IllegalStateException("未返回播放地址");
        }
        return vo;
    }
}
