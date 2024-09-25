package org.test.jakarta.hello.repository;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.test.jakarta.hello.model.UploadedFile;
@Stateless
public class UploadedFileRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(UploadedFile file) {
        entityManager.persist(file);
    }
}
