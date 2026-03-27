package com.im.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FileUploadRequestDTO {

    private String fileName;
    private String fileHash;
    private Long fileSize;
    private String mimeType;
    private Integer chunkSize;
    private Integer totalChunks;
    private Long conversationId;
    private String checksum;

    // Getters and Setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public Integer getChunkSize() { return chunkSize; }
    public void setChunkSize(Integer chunkSize) { this.chunkSize = chunkSize; }

    public Integer getTotalChunks() { return totalChunks; }
    public void setTotalChunks(Integer totalChunks) { this.totalChunks = totalChunks; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }

    @Override
    public String toString() {
        return "FileUploadRequestDTO{fileName='" + fileName + "', fileSize=" + fileSize + "}'";
    }
}
