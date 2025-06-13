package model.repository;

import java.util.List;

public interface UserRepository<T, K> {
    T save(T t);
    List<T> findAll();
    K delete(K id);
}
