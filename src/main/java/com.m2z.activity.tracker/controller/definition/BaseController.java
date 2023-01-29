package com.m2z.activity.tracker.controller.definition;

import com.m2z.activity.tracker.service.definition.BaseService;
import lombok.Getter;

@Getter
public abstract class BaseController<T, P, I> {
    private final BaseService<T, P, I> service;

    public BaseController(BaseService<T, P, I> service) {
        this.service = service;
    }

//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public List<P> getAll()
//    {
//        return service.findAll();
//    }
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public P create(@RequestBody T element){
//        return service.save(element);
//    }
//
//    @PutMapping
//    @ResponseStatus(HttpStatus.OK)
//    public P update(@RequestBody T changedT){
//        return service.save(changedT);
//    }
//
//    @DeleteMapping("{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public void delete(@PathVariable("id") I id){
//        service.delete(id);
//    }
//
//    @GetMapping(path = "{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public P getOne(@PathVariable("id") I id){
//        return service.findOne(id);
//    }
//
//    @GetMapping(path = "/find")
//    @ResponseStatus(HttpStatus.OK)
//    public Page<P> find(@RequestBody List<SearchCriteria> searchCriteria)
//    {
//        return new PageImpl<P>(service.search(searchCriteria));
//    }

}
