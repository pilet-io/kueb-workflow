package io.pilet.kueb.workflow.controller;

import io.pilet.kueb.workflow.services.Stores;
import io.pilet.kueb.workflow.model.iam.IamUser;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/iam/users")
public class IamController {
    private final Stores stores;

    public IamController(Stores stores) {
        this.stores = stores;
    }

    @GetMapping("/by-email/{email}")
    public IamUser getUserByEmail(@PathVariable String email) {
        return stores.of(IamUser.class).get(email);
    }

}
