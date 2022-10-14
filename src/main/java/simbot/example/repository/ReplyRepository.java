package simbot.example.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import simbot.example.entity.Reply;

import java.util.List;

/**
 * {@link Reply} 持久层交互
 *
 * @author ForteScarlet
 */
@Repository
public interface ReplyRepository extends JpaRepositoryImplementation<Reply, Long> {

    /**
     * 根据关键词查询结果
     *
     * @param keyword 关键词
     * @return found
     */
    Reply findByKeyword(String keyword);


    /**
     * 根据关键词删除。
     *
     * @param keyword keyword
     */
    @Modifying
    void deleteByKeyword(String keyword);


}
