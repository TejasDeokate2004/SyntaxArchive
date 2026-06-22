package com.logicarchive.repository;

import com.logicarchive.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findByUserId(Long userId);

    Optional<Folder> findByIdAndUserId(Long folderId, Long userId);

    boolean existsByNameAndUserId(String name, Long userId);

    Optional<Folder> findByNameAndUserId(String name, Long userId);
}
