package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))")
    List<Item> search(String text, Pageable pageable);

    @Query(value = "select user_id from items where id = ?1",
        nativeQuery = true)
    Long getOwnerId(Long id);

    @Query(value = "select * from items where request_id = ?1", nativeQuery = true)
    List<Item> findItemsByRequestId(Long requestId);
}
