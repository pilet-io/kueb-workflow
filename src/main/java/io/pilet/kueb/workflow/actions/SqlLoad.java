package io.pilet.kueb.workflow.actions;


import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;


@Action(name = "sql/load", description = "Load sql files", params = {"folder,files"})
public class SqlLoad implements ActionProcessor {
    static final Logger log = LoggerFactory.getLogger(SqlLoad.class);

    @Override
    public ActionResult process(Context context, String argsJson) {
        Args args = Json.map(argsJson, Args.class);
        List<String> logs = new LinkedList<>();
        for(String file : args.files) {
            Path filePath = Paths.get(args.folder + "/" + file);
            log.info("Loading file {}", filePath);
            try {
                String sql = Files.readString(filePath);
                context.runtime().jdbcTemplate().execute(sql);
            } catch (IOException e) {
                throw new ProcessingException(ErrorCode.SYSTEM_CONFIG_ERROR, "Failed to load sql file " + file, e);
            }
        }
        return new ActionResult(HttpStatus.OK, Params.EMPTY, logs);
    }

    record Args (
            String folder,
            String[] files
    ) {
    }
}
