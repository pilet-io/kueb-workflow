package io.pilet.kueb.workflow.services;

import io.pilet.kueb.common.util.Folder;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.common.util.Yaml;
import io.pilet.kueb.workflow.common.Actions;
import io.pilet.kueb.workflow.common.AppVersions;
import io.pilet.kueb.workflow.common.RawScript;
import io.pilet.kueb.workflow.config.AppConfig;
import io.pilet.kueb.workflow.model.ActionScript;
import io.pilet.kueb.workflow.model.AppVersion;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.pilet.kueb.common.data.Filter.eq;

@Service
@Log4j2
public class InitService {

    public InitService(AppConfig appConfig,
                       Stores stores,
                       RunExecutorService executorService) {
        Actions.scanActions("io.pilet.kueb.workflow.actions");
        Folder.scan(appConfig.getConfigPath() + "/workflows", ".yaml", keyValue -> {
            log.info("Found workflow definition: {}", keyValue.key());
            RawScript rawScript = Yaml.map(keyValue.value(), RawScript.class);
            ActionScript actionScript = ActionScript.of(rawScript,keyValue.value());
            stores.of(ActionScript.class).save(actionScript);
        });
        // -- Loading App Versions
        List<AppVersion> appVersions = stores.of(AppVersion.class).list(eq("preferred", "Y"));
        for(AppVersion appVersion : appVersions) {
            AppVersions.register(appVersion.app(), appVersion.version());
        }
        log.info("Loaded {} app versions", appVersions.size());
        executorService.sync("system", "app-init", Params.EMPTY);
    }

}
