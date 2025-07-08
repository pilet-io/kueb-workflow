package io.pilet.kueb.workflow.actions;


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


@Action(name = "dns/delete", description = "DNS delete entry", params = {"id,dns,zone"})
public class DnsDelete implements ActionProcessor {
    static final Logger log = LoggerFactory.getLogger(DnsDelete.class);

    @Override
    public ActionResult process(Context context, String argsJson) {
        Args args = Json.map(argsJson, Args.class);
        List<String> logs = new LinkedList<>();
        DnsZone dnsZone = context.runtime().stores().of(DnsZone.class).get(args.zone());
        String dnsPassword = System.getenv().getOrDefault("DNS_PASSWORD", "");
        log.info("DnsDelete, dnsZone : {}", dnsZone);
        String url = "https://api.cloudflare.com/client/v4/zones/" + dnsZone.id() + "/dns_records/" + args.id();
        logs.add("DnsDelete url: " + url);
        try (HttpClient client = HttpClient.newBuilder().build()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("X-Auth-Email", "potapov@yahoo.com")
                    .header("X-Auth-Key", dnsPassword)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Params params = Json.map(response.body(), Params.class);
            logs.add("DnsDelete response: " + params);
            return new ActionResult(HttpStatus.OK, params, logs);

        } catch (IOException | InterruptedException e) {
            throw new ProcessingException(ErrorCode.REST_ERROR, e);
        }
    }

    record Args (
            String id,
            String dns,
            String zone
    ) {}

}
