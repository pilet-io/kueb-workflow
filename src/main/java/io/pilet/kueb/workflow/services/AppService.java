package io.pilet.kueb.workflow.services;

import io.pilet.kueb.common.data.Filter;
import io.pilet.kueb.common.data.Store;
import io.pilet.kueb.workflow.model.App;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppService implements Store<App> {
    private final Stores stores;

    public AppService(Stores stores) {
        this.stores = stores;
    }

    @Override
    public App save(App data) {
        return stores.of(App.class).save(data);
    }

    @Override
    public void delete(String... id) {
        stores.of(App.class).delete(id);
    }

    @Override
    public List<App> list(Filter... filters) {
        return stores.of(App.class).list(filters);
    }

    @Override
    public App get(String... id) {
        return stores.of(App.class).get(id);
    }
}
