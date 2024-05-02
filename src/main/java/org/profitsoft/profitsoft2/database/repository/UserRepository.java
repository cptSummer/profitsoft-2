package org.profitsoft.profitsoft2.database.repository;

import org.profitsoft.profitsoft2.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
