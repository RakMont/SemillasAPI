package com.seedproject.seed.models.dto;

import com.seedproject.seed.models.entities.CommentRecord;
import com.seedproject.seed.models.entities.Volunter;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CommentRecordDTO {
    private String commentId;
    private String comment;
    private Date comment_date;


    public CommentRecordDTO(String id,CommentRecord commentRecord) {
        this.commentId = id;
        this.comment = commentRecord.getComment();
        this.comment_date = commentRecord.getComment_date();
    }

    public CommentRecordDTO() {
    }

    public CommentRecord getCommentRecord(CommentRecordDTO commentRecordDTO, Volunter volunteer){
        CommentRecord res = new CommentRecord();
        res.setComment_record_id(commentRecordDTO.getCommentId() != null ?
                Long.parseLong(commentRecordDTO.getCommentId()) : null);
        res.setComment(commentRecordDTO.getComment());
        res.setComment_date(commentRecordDTO.getComment_date());
        res.setRegisterVolunteer(volunteer);
        return res;
    }
}
