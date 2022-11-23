package com.seedproject.seed.models.dao;

import com.seedproject.seed.models.entities.CommentRecord;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRecordDao {
    private String commentRecordId;
    private String comment;
    private String registerVolunteerId;
}
