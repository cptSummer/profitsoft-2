package org.profitsoft.profitsoft2.database.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/**
 * A service class for handling database operations.
 *
 * @param <E> The entity type handled by this service.
 * @param <T> The JpaRepository type associated with the entity.
 */
@Service
public abstract class DefaultService<E, T extends JpaRepository> {
    @Autowired
    T jpaRepository;

    public List<E> findAll() {
        return jpaRepository.findAll();
    }

    public void save(E e){
        jpaRepository.save(e);
    }

    public void saveAll(List<E> e){
        jpaRepository.saveAll(e);
    }

    public void delete(E e){
        jpaRepository.delete(e);
    }

    public void deleteById(int id){
        jpaRepository.deleteById(id);
    }

    public Optional<E> getById(int id) {
       return jpaRepository.findById(id);
    }

    public long count(){
        return jpaRepository.count();
    }
}
