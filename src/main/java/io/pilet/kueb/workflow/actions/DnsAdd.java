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
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;


@Action(name = "dns/add", description = "DNS add entry", params = {"dns,zone,cname"})
public class DnsAdd implements ActionProcessor {
    static final Logger log = LoggerFactory.getLogger(DnsAdd.class);

    @Override
    public ActionResult process(Context context, String argsJson) {
        Args args = Json.map(argsJson, Args.class);
        List<String> logs = new LinkedList<>();
        DnsZone dnsZone = context.runtime().stores().of(DnsZone.class).get(args.zone());
        log.info("DnsAdd, dnsZone : {}", dnsZone);
        String dnsPassword = System.getenv().getOrDefault("DNS_PASSWORD", "");
        String url = "https://api.cloudflare.com/client/v4/zones/" + dnsZone.id() + "/dns_records";
        CloudflareEntry entry = new CloudflareEntry("CNAME",
                args.dns() + "." + args.zone(), args.cname(), false);
        logs.add("DnsAdd request: " + entry);
        HttpClient client = HttpClient.newBuilder().build();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("X-Auth-Email", "potapov@yahoo.com")
                    .header("X-Auth-Key", dnsPassword)
                    .POST(BodyPublishers.ofString(Json.of(entry)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Params params = Json.map(response.body(), Params.class);
            logs.add("DnsAdd response: " + params);
            return new ActionResult(HttpStatus.OK, params, logs);

        } catch (IOException | InterruptedException e) {
            throw new ProcessingException(ErrorCode.REST_ERROR, e);
        }
    }

    record Args (
            String dns,
            String zone,
            String cname
    ) {}

    record CloudflareEntry (
            String type,
            String name,
            String content,
            boolean proxied
    ) {}

}
