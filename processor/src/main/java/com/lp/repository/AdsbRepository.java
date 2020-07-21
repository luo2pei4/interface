package com.lp.repository;

import com.lp.entity.AdsbEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "adsbRepository")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public interface AdsbRepository extends JpaRepository<AdsbEntity, Long> {

}
