package com.lp.repository;

import com.lp.entity.FlightInfoEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository(value = "flightInfoRepository")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public interface FlightInfoRepository extends JpaRepository<FlightInfoEntity, Long> {

	@Query(value = "select f from FlightInfoEntity f where f.an = ?1")
    FlightInfoEntity searchEntityByAn(String an);
}
