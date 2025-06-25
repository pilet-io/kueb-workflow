package io.pilet.kueb.workflow.actions;

import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.lang.Runtime;
import java.util.LinkedList;
import java.util.List;

@Action(name = "ssh", description = "Run ssh command", params = {"host","command"})
public class SshAction implements ActionProcessor {
    static Logger log = LoggerFactory.getLogger(SshAction.class);

    @Override
    public ActionResult process(Context context, String argsJson) {
        Args args = Json.map(argsJson, Args.class);
        Result result = executeSsh(args.host(), args.command());
        if(result.code() > 0)
            return new ActionResult(HttpStatus.CONFLICT, result.params(), result.error());
        else
            return new ActionResult(HttpStatus.OK, result.params(), result.console());
    }

    record Args (
            String host,
            String command
    ) {
    }

    public static Result executeSsh(String host, String cmd) {
        String command = String.format("ssh -i ~/.ssh/hetz_id -o StrictHostKeyChecking=accept-new root@%s \"bash -lc '%s'\"", host, cmd);
        return execute(command);
    }



    public static Result execute(String cmd) {
        log.info("Bash : {}", cmd);
        Runtime runtime = Runtime.getRuntime();
        String[] commands = {"bash", "-c", cmd};
        try {
            Process p = runtime.exec(commands);
            BufferedReader input = p.inputReader();
            BufferedReader error = p.errorReader();

            List<String> console = new LinkedList<>();
            String line = "";
            while ((line = input.readLine()) != null) {
                log.info("Bash : {}", line);
                console.add(line);
            }
            input.close();

            List<String> err = new LinkedList<>();
            while ((line = error.readLine()) != null) {
                log.error("Bash error : {}", line);
                err.add(line);
            }
            error.close();
            p.waitFor();

            Result result = new Result(p.exitValue(), extractParams(console), console, err);
            log.info("Ran ssh {} with {} {}", cmd, result.code(), result.params());
            return result;
        } catch (Exception e) {
            throw new ProcessingException(ErrorCode.SSH_EXECUTION_ERROR, e.getMessage());
        }
    }

    public static Params extractParams(List<String> console) {
        Params params = new Params();
        for(String line : console) {
            if(line.startsWith("#>")) {
                String[] parts = line.substring(2).split("=");
                if (parts.length == 2) {
                    params.put(parts[0].strip(), parts[1].strip());
                }
            }
        }
        return params;
    }


    public record Result (
            int code,
            Params params,
            List<String> console,
            List<String> error
    ) {}

}
