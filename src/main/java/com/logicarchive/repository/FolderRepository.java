package com.logicarchive.repository;

import com.logicarchive.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findByUserId(Long userId);

    Optional<Folder> findByIdAndUserId(Long id, Long userId);
}
