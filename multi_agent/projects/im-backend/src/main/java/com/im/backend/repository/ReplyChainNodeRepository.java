package com.im.backend.repository;

import com.im.backend.entity.ReplyChainNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReplyChainNodeRepository extends JpaRepository<ReplyChainNode, Long> {
    
    List<ReplyChainNode> findByChainIdOrderByPositionInBranchAsc(Long chainId);
    
    List<ReplyChainNode> findByMessageId(Long messageId);
    
    Optional<ReplyChainNode> findByChainIdAndMessageId(Long chainId, Long messageId);
    
    @Query("SELECT n FROM ReplyChainNode n WHERE n.chainId = :chainId AND n.isDeleted = false ORDER BY n.positionInBranch ASC")
    List<ReplyChainNode> findActiveNodesByChain(@Param("chainId") Long chainId);
    
    @Query("SELECT COUNT(n) FROM ReplyChainNode n WHERE n.chainId = :chainId")
    Long countByChainId(@Param("chainId") Long chainId);
    
    void deleteByChainId(Long chainId);
    
    void deleteByMessageId(Long messageId);
}
