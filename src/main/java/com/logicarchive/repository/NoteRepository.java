package com.logicarchive.repository;

import com.logicarchive.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserId(Long userId);

    List<Note> findByFolderIdAndUserId(Long folderId, Long userId);

    Optional<Note> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(n.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Note> searchByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
}
