package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query(nativeQuery = true, value = "select * from requests where user_id = ?1")
    List<ItemRequest> findAllByRequesterId(Long id);

    @Query(nativeQuery = true, value = "select * from requests where id <> ?1")
    Page<ItemRequest> findAll(Long requesterId,Pageable pageable);
}
