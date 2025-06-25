package io.pilet.kueb.workflow.controller;

import io.pilet.kueb.common.data.Stores;
import io.pilet.kueb.workflow.model.iam.IamUser;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/iam/users")
public class IamController {

    @GetMapping("/by-email/{email}")
    public IamUser getUserByEmail(@PathVariable String email) {
        return Stores.of(IamUser.class).get(email);
    }

}
