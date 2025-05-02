package jp.co.metateam.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import jp.co.metateam.library.model.Account;

public interface AccountRepository extends JpaRepository<Account, String> {

	@Query(value = "SELECT * FROM accounts WHERE email = ?1", nativeQuery = true)
	Account selectByEmail(String email);

	@Query(value = "SELECT * FROM accounts WHERE employee_id = ?1", nativeQuery = true)
	Optional<Account> selectByEmployeeId(String employeeId);

}
