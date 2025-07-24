package xyz.moodf.global.file.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import xyz.moodf.global.file.entities.FileInfo;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long>, QuerydslPredicateExecutor<FileInfo> {

}
