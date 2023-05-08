package com.m2z.activity.tracker.service.definition;

import com.m2z.activity.tracker.dto.SearchCriteria;
import com.m2z.activity.tracker.entity.Project;
import com.m2z.activity.tracker.repository.definition.BaseRepository;
import com.m2z.activity.tracker.repository.definition.SearchQueryConsumer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class BaseService<T, P, I>{
    private final BaseRepository<T, I> repository;
    @PersistenceContext
    protected EntityManager entityManager;

    public BaseService(BaseRepository<T, I> repository) {
        this.repository = repository;
    }

    public abstract P convertToDTO(T element);

    public List<P> generateList(Iterable<T> elements) {
        List<P> newList = new ArrayList<>();
        elements.forEach(element -> {
            newList.add(this.convertToDTO(element));

        });
        return newList;
    }

    public List<P> findAll(){
        return this.generateList(repository.findAll());
    }

    public P findOne(I id){
        return this.convertToDTO(repository.getById(id));
    }

    public void delete(I id){
        repository.deleteById(id);
    }

    public P save(T expenseGroup){
        return this.convertToDTO(repository.save(expenseGroup));
    }


    public List<P> search(List<SearchCriteria> searchCriteria){
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Project> query = builder.createQuery(Project.class);
        Root<Project> r = query.from(Project.class);
        Predicate predicate = builder.conjunction();

        SearchQueryConsumer searchConsumer =
                new SearchQueryConsumer(predicate, builder, r);
        searchCriteria.forEach(searchConsumer);
        predicate = searchConsumer.getPredicate();
        query.where(predicate);

        return null;
//        return this.generateList(entityManager.createQuery(query).getResultList());
    }
}
