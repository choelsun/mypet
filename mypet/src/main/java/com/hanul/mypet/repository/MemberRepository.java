package com.hanul.mypet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanul.mypet.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, String>{
	
	boolean existsByEmail(String email);

	Optional<MemberEntity> findByEmail(String email);

	
	@EntityGraph(attributePaths = {"roleSet"}, type = EntityGraph.EntityGraphType.LOAD)
	@Query("SELECT m FROM MemberEntity m WHERE m.fromSocial=:social AND m.email=:email")
	Optional<MemberEntity> findByEmail(@Param("email") String email, @Param("social") boolean social);
}
