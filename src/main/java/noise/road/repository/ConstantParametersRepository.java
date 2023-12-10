package noise.road.repository;

import org.springframework.data.repository.CrudRepository;

import noise.road.entity.ConstantParameters;

public interface ConstantParametersRepository extends CrudRepository<ConstantParameters, Integer> {

	//ConstantParameters findFirst();
}
