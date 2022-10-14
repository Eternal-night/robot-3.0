package simbot.example.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import simbot.example.entity.Reply;
import simbot.example.service.ReplyService;

import java.util.List;

/**
 * @author ForteScarlet
 */
@RestController
@RequestMapping("/reply")
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;


    /**
     * 根据条件查询所有匹配数据.
     * @param condition condition
     * @return data list.
     */
    @GetMapping("/list")
    public List<Reply> replies(Reply condition) {
        return replyService.replies(condition);
    }

    /**
     * 根据关键词查询其对应的结果。
     * @param keyword keyword
     * @return 回复语句
     */
    @GetMapping("/keyword/{keyword}")
    public String reply(@PathVariable String keyword) {
        return replyService.reply(keyword);
    }

    /**
     * 新增一个关键词回复信息。
     *
     * @param reply data entity.
     * @return inserted entity.
     */
    @PostMapping
    public Reply add(@RequestBody Reply reply) {
        return replyService.addReply(reply.getKeyword(), reply.getContent());
    }

    /**
     * 根据关键词删除reply信息。
     *
     * @param keyword keyword
     */
    @DeleteMapping("/keyword/{keyword}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String keyword) {
        replyService.deleteReply(keyword);
    }

}
