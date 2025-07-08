package io.pilet.kueb.workflow.actions;


import io.pilet.kueb.common.data.Store;
import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.*;
import io.pilet.kueb.workflow.model.DnsZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Action(name = "dns/zone-list", description = "List and save DNS zones in DB", params = {""})
public class DnsZoneList implements ActionProcessor {
    static final Logger log = LoggerFactory.getLogger(DnsZoneList.class);

    @Override
    public ActionResult process(Context context, String argsJson) {
        List<String> logs = new LinkedList<>();
        String dnsPassword = System.getenv().getOrDefault("DNS_PASSWORD", "");
        log.info("dns/zone-list with password : {}", dnsPassword);
        try (HttpClient client = HttpClient.newBuilder().build()) {
            HttpRequest request = HttpRequest.newBuilder().GET()
                    .uri(URI.create("https://api.cloudflare.com/client/v4/zones"))
                    .header("Content-Type", "application/json")
                    .header("X-Auth-Email", "potapov@yahoo.com")
                    .header("X-Auth-Key", dnsPassword)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Params params = Json.map(response.body(), Params.class);
            List<Map> zones = params.getList("result", Map.class);
            Store<DnsZone> zoneStore = context.runtime().stores().of(DnsZone.class);
            for(Map zone : zones) {
                DnsZone dnsZone = new DnsZone(zone.get("id").toString(),
                        zone.get("name").toString(), zone.get("status").toString());
                zoneStore.save(dnsZone);
            }

        } catch (IOException | InterruptedException e) {
            throw new ProcessingException(ErrorCode.REST_ERROR, e);
        }
        return new ActionResult(HttpStatus.OK, Params.EMPTY, logs);
    }

}
