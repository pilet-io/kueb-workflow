package io.pilet.kueb.workflow.controller;

import io.pilet.kueb.workflow.model.Car;
import io.pilet.kueb.workflow.model.Person;
import io.pilet.kueb.workflow.model.iam.IamUser;
import io.pilet.kueb.workflow.services.Stores;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.pilet.kueb.common.data.Filter.eq;

@RestController
@RequestMapping("/v1/entity/people")
public class PeopleController {
    private final Stores stores;

    public PeopleController(Stores stores) {
        this.stores = stores;
    }

    @PostMapping()
    Person createPerson(@RequestBody Person requested, @RequestAttribute IamUser user) {
        Person person = new Person(user.org(), requested.name(), requested.age(), requested.zip());
        return stores.of(Person.class).save(person);
    }

    @GetMapping()
    List<Person> getPeople(@RequestAttribute IamUser user) {
        return stores.of(Person.class).list(eq("org", user.org()));
    }

    @DeleteMapping("/{name}")
    void deleteKueb(@PathVariable String name, @RequestAttribute IamUser user) {
        stores.of(Person.class).delete(user.org(), name);
    }

    @PostMapping("/{name}/cars")
    Car createCar(@PathVariable String name, @RequestBody Car requested, @RequestAttribute IamUser user) {
        Car car = new Car(user.org(), name, requested.model(), requested.year());
        return stores.of(Car.class).save(car);
    }

    @GetMapping("/{name}/cars")
    List<Car> getCars(@PathVariable String name, @RequestAttribute IamUser user) {
        return stores.of(Car.class).list(eq("org", user.org()), eq("name", name));
    }

    @DeleteMapping("/{name}/cars/{model}")
    void deleteCar(@PathVariable String name, @PathVariable String model, @RequestAttribute IamUser user) {
        stores.of(Car.class).delete(user.org(), name, model);
    }

}
