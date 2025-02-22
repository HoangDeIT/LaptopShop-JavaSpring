package com.project.LaptopShop.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.LaptopShop.domain.User;
import com.project.LaptopShop.domain.response.RegisterDTO;
import com.project.LaptopShop.domain.response.ResUserDTO;
import com.project.LaptopShop.domain.response.ResultPaginationDTO;
import com.project.LaptopShop.repository.UserRepository;
import com.project.LaptopShop.util.constant.TypeEnum;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FilterParser filterParser;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public UserService(UserRepository userRepository, FilterParser filterParser,
            FilterSpecificationConverter filterSpecificationConverter) {
        this.userRepository = userRepository;
        this.filterParser = filterParser;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    public User getUserByUsername(String username) {

        Optional<User> userOptional = this.userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public RegisterDTO registerUser(User user) {
        user = this.userRepository.save(user);
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setId(user.getId());
        registerDTO.setUserName(user.getUserName());
        registerDTO.setEmail(user.getEmail());
        registerDTO.setCreatedAt(user.getCreatedAt());
        registerDTO.setCreatedBy(user.getCreatedBy());
        registerDTO.setRole(user.getRole());
        return registerDTO;
    }

    public User getUserByUserName(String username) {
        Optional<User> userOptional = this.userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public User getUserByUserNameAndType(String username, TypeEnum type) {
        Optional<User> userOptional = this.userRepository.findByUserNameAndType(username, type);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public ResUserDTO getUser(User user) {
        if (user == null) {
            return null;
        }
        ResUserDTO registerDTO = new ResUserDTO();
        registerDTO.setId(user.getId());
        registerDTO.setUserName(user.getUserName());
        registerDTO.setEmail(user.getEmail());
        registerDTO.setRole(user.getRole());
        registerDTO.setType(user.getType());
        registerDTO.setImage(user.getImage());
        registerDTO.setCreatedAt(user.getCreatedAt());
        registerDTO.setUpdatedAt(user.getUpdatedAt());
        registerDTO.setCreatedBy(user.getCreatedBy());
        registerDTO.setUpdatedBy(user.getUpdatedBy());
        return registerDTO;
    }

    public ResultPaginationDTO fetchUser(Pageable pageable, Specification<User> spec) {
        FilterNode node = filterParser.parse("deleted='" + false + "'");
        FilterSpecification<User> spec1 = filterSpecificationConverter.convert(node);
        spec = spec.and(spec1);
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);

        // Adjust page number if it exceeds total pages
        int totalPages = pageUser.getTotalPages();
        int pageNumber = Math.min(pageable.getPageNumber(), totalPages - 1);
        if (pageNumber != pageable.getPageNumber()) {
            pageable = pageable.withPage(totalPages - 1);
            pageUser = this.userRepository.findAll(spec, pageable);
        }
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());
        res.setMeta(meta);

        List<ResUserDTO> users = pageUser.getContent().stream().map(this::getUser).toList();
        res.setResult(users);
        return res;
    }

    public ResUserDTO changeRole(User user) {
        User userDB = this.userRepository.findById(user.getId()).get();
        if (userDB == null)
            return null;
        userDB.setRole(user.getRole());
        return getUser(this.userRepository.save(userDB));
    }

    public String deleteUser(long id) {
        User user = this.userRepository.findById(id).get();
        user.setDeleted(true);
        user.setDeletedAt(Instant.now());
        this.userRepository.save(user);
        return "OK";
    }

    @Transactional
    public ResUserDTO rollbackDelete(long userId) {
        User user = this.userRepository.findById(userId).get();
        user.setDeleted(false);
        user.setDeletedAt(null);
        this.userRepository.save(user);

        return this.getUser(this.userRepository.save(user));
    }

    @Scheduled(fixedDelay = 20000)
    @Transactional
    public void hardDeleteExpiredUsers() {
        Instant cutoffTime = Instant.now().minusSeconds(20);
        List<User> usersToDelete = this.userRepository.findByDeletedTrueAndDeletedAtBefore(cutoffTime);
        if (!usersToDelete.isEmpty()) {
            this.userRepository.deleteAll(usersToDelete);
        }
    }
}
