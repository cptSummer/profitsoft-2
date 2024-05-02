package org.profitsoft.profitsoft2.database.repository;

import org.profitsoft.profitsoft2.database.entity.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Integer> {
    String filteredQuery = "select * from photos p " +
            "where (:photoName is null or photo_name ~* CONCAT('\\y', :photoName, '\\y')) " +
            "and (:photoFormat is null or photo_format = :photoFormat) " +
            "and (:uploadDate is null or upload_date = cast(:uploadDate as date));";

    @Query(value = filteredQuery,
            countQuery = """
                    select count(p.id) from photos p
                    where (:photoName is null or p.photo_name ~* CONCAT('\\y', :photoName, '\\y'))
                    and (:photoFormat is null or p.photo_format = :photoFormat)
                    and (:uploadDate is null or p.upload_date =  cast(:uploadDate as date));
                    """,
            nativeQuery = true)
    Page<Photo> getPhotosPagination(Pageable page,
                                    @Param("photoName") String photoName,
                                    @Param("photoFormat") String photoFormat,
                                    @Param("uploadDate") String uploadDate);


    @Query(value = filteredQuery, nativeQuery = true)
    List<Photo> getPhotosByFilters(@Param("photoName") String photoName,
                                   @Param("photoFormat") String photoFormat,
                                   @Param("uploadDate") String uploadDate);


}
