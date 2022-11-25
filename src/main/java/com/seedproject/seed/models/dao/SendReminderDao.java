package com.seedproject.seed.models.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SendReminderDao {
   private List<String> seedsIds;
   private List<String> seedsEmails;
   private String emailSubject;
   private String emailBody;
}
