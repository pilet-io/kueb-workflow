package io.pilet.kueb.workflow.services;

import io.pilet.kueb.common.data.Stores;
import io.pilet.kueb.common.util.Folder;
import io.pilet.kueb.common.util.Yaml;
import io.pilet.kueb.workflow.common.Actions;
import io.pilet.kueb.workflow.common.RawScript;
import io.pilet.kueb.workflow.common.Runtime;
import io.pilet.kueb.workflow.config.AppConfig;
import io.pilet.kueb.workflow.model.ActionScript;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Log4j2
public class InitService {

    public InitService(AppConfig appConfig,
                       KafkaTemplate<String, String> kafkaTemplate,
                       JdbcTemplate jdbcTemplate,
                       RunExecutorService executorService) {
        initSql(jdbcTemplate, appConfig.getSqlPath() + "/iam.sql");
        initSql(jdbcTemplate, appConfig.getSqlPath() + "/org.sql");
        initSql(jdbcTemplate, appConfig.getSqlPath() + "/system.sql");
        initSql(jdbcTemplate, appConfig.getSqlPath() + "/workflow.sql");

        Runtime.init(jdbcTemplate, executorService);
        Stores.init(kafkaTemplate, jdbcTemplate, "io.pilet.kueb.workflow.model");
        Stores.init(kafkaTemplate, jdbcTemplate, "io.pilet.kueb.workflow.model.iam");
        Actions.scanActions("io.pilet.kueb.workflow.actions");
        Folder.scan(appConfig.getConfigPath() + "/workflows", ".yaml", keyValue -> {
            log.info("Found workflow definition: {}", keyValue.key());
            RawScript rawScript = Yaml.map(keyValue.value(), RawScript.class);
            ActionScript actionScript = ActionScript.of(rawScript,keyValue.value());
            Stores.of(ActionScript.class).save(actionScript);
        });
    }

    @SneakyThrows
    public void initSql(JdbcTemplate jdbcTemplate, String filename) {
        String sql = Files.readString(Paths.get(filename));
        jdbcTemplate.execute(sql);
    }
}
