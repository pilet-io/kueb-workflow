package io.pilet.kueb.workflow.services;

import io.pilet.kueb.common.data.Filter;
import io.pilet.kueb.workflow.model.Kueb;
import io.pilet.kueb.workflow.model.KuebFull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KuebService {
    private final Stores stores;

    public KuebService(Stores stores) {
        this.stores = stores;
    }

    public Kueb save(Kueb data) {
        return stores.of(Kueb.class).save(data);
    }

    public void delete(String... id) {
        stores.of(Kueb.class).delete(id);
    }

    public List<Kueb> list(Filter... filters) {
        return stores.of(Kueb.class).list(filters);
    }

    public KuebFull get(String... id) {
        return stores.of(KuebFull.class).get(id);
    }
}
