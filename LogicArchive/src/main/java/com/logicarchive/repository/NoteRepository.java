package com.logicarchive.repository;

import com.logicarchive.entity.Note;
import com.logicarchive.enums.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByFolderIdAndIsDeletedFalse(Long folderId);

    List<Note> findByUserIdAndIsDeletedFalse(Long userId);

    Optional<Note> findByIdAndUserIdAndIsDeletedFalse(Long noteId, Long userId);

    Optional<Note> findByIdAndIsDeletedFalse(Long noteId);

    List<Note> findByUserIdAndIsDeletedTrue(Long userId);

    List<Note> findByIsDeletedTrue();

    boolean existsByTitleAndFolderIdAndIsDeletedFalse(String title, Long folderId);

    @Query("SELECT n FROM Note n WHERE n.isDeleted = false AND n.user.id = :userId " +
            "AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(n.codeFileName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Note> searchByTitleOrCodeFileName(@Param("userId") Long userId, @Param("keyword") String keyword);

    List<Note> findByVisibilityAndIsDeletedFalse(Visibility visibility);

    List<Note> findByIsDeletedFalse();

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Note n " +
            "WHERE n.title = :title AND n.folder.id = :folderId AND n.id != :noteId AND n.isDeleted = false")
    boolean existsByTitleAndFolderIdExcludingNoteId(@Param("title") String title,
                                                     @Param("folderId") Long folderId,
                                                     @Param("noteId") Long noteId);
}
