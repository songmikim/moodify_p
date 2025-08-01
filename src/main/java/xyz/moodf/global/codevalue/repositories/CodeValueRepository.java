package xyz.moodf.global.codevalue.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.moodf.global.codevalue.entities.CodeValue;

public interface CodeValueRepository extends JpaRepository<CodeValue, String> {
}
