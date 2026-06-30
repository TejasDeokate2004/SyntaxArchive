package com.logicarchive.service;

import com.logicarchive.dto.FolderRequest;
import com.logicarchive.dto.FolderResponse;
import com.logicarchive.entity.Folder;
import com.logicarchive.entity.User;
import com.logicarchive.exception.ResourceNotFoundException;
import com.logicarchive.repository.FolderRepository;
import com.logicarchive.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    public FolderService(FolderRepository folderRepository, UserRepository userRepository) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public FolderResponse createFolder(Long userId, FolderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Folder folder = new Folder();
        folder.setName(request.getName());
        folder.setDescription(request.getDescription());
        folder.setUser(user);

        return new FolderResponse(folderRepository.save(folder));
    }

    @Transactional
    public FolderResponse updateFolder(Long userId, Long folderId, FolderRequest request) {
        Folder folder = findFolder(folderId, userId);

        if (request.getName() != null && !request.getName().isBlank()) {
            folder.setName(request.getName());
        }
        if (request.getDescription() != null) {
            folder.setDescription(request.getDescription());
        }

        return new FolderResponse(folderRepository.save(folder));
    }

    public FolderResponse getFolder(Long userId, Long folderId) {
        return new FolderResponse(findFolder(folderId, userId));
    }

    public List<FolderResponse> getAllFolders(Long userId) {
        return folderRepository.findByUserId(userId).stream()
                .map(FolderResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteFolder(Long userId, Long folderId) {
        Folder folder = findFolder(folderId, userId);
        folderRepository.delete(folder);
    }

    private Folder findFolder(Long folderId, Long userId) {
        return folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not found with id: " + folderId));
    }
}
