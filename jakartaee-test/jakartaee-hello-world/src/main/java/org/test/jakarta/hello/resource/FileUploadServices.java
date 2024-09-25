package org.test.jakarta.hello.resource;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.test.jakarta.hello.model.UploadedFile;
import org.test.jakarta.hello.repository.UploadedFileRepository;
import java.sql.Timestamp;

@Stateless
public class FileUploadServices {

    @Inject
    private UploadedFileRepository uploadedFileRepository;

    public void saveFile(String fileName, byte[] fileData) {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(fileName);
        uploadedFile.setFileData(fileData);
        uploadedFile.setUploadedAt(new Timestamp(System.currentTimeMillis()));
        uploadedFileRepository.save(uploadedFile);
    }
}
