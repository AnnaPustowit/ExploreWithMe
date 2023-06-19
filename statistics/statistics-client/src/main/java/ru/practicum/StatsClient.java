package ru.practicum;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class StatsClient {

    static final CloseableHttpClient httpClient = HttpClients.createDefault();

    public void saveStats(String app, String uri, String ip, String timestamp) throws Exception {
        HttpPost request = new HttpPost("http://localhost:9090/hit");
        final EndpointHitDto endpointHit = new EndpointHitDto(app, uri, ip, timestamp);
        ObjectMapper mapper = new ObjectMapper();
        StringEntity json = new StringEntity(mapper.writeValueAsString(endpointHit), ContentType.APPLICATION_JSON);
        request.setEntity(json);
        CloseableHttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() != 200) {
            log.info("Ошибка - " + response.getStatusLine().getStatusCode());
        }
    }

    public List<ViewStatsDto> getStats() throws Exception {
        HttpGet request = new HttpGet("http://localhost:9090/stats");
        CloseableHttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        if (response.getStatusLine().getStatusCode() != 200) {
            log.info("Ошибка - " + response.getStatusLine().getStatusCode());
        }

        ObjectMapper mapper = new ObjectMapper();
        List<ViewStatsDto> statsList = new ArrayList<>();
        statsList = mapper.readValue(EntityUtils.toString(entity), new TypeReference<List<ViewStatsDto>>() {
        });
        return statsList;
    }
}
