package com.api;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttachment;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.List;

public class AppointmentsObject {
    private String appointmentBody;
    private List<EventAttendee> attendees;
    private String bodyPreview;
    private String changeKey;
    private String createdDateTime;
    private Event.Source domain;
    private String end;
    private String endDate;
    private List<EventAttachment> hasAttachments;
    private String iCalUId;
    private String id;
    private String importance;
    private String isAllDay;
    private String isCancelled;
    private Event.Organizer isOrganizer;
    private String isReminderOn;
    private String location;
    private String onlineMeetingUrl;
    private Event.Organizer organizer;
    private String originalEndTimeZone;
    private String rawBody;
    private List<String> recurrence;
    private String responseRequested;
    private EventDateTime start;
    private String startDate;
    private String subject;
    private String type;
    private String userId;

    public String getAppointmentBody() {
        return appointmentBody;
    }

    public void setAppointmentBody(String appointmentBody) {
        this.appointmentBody = appointmentBody;
    }

    public List<EventAttendee> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<EventAttendee> attendees) {
        this.attendees = attendees;
    }

    public String getBodyPreview() {
        return bodyPreview;
    }

    public void setBodyPreview(String bodyPreview) {
        this.bodyPreview = bodyPreview;
    }

    public String getChangeKey() {
        return changeKey;
    }

    public void setChangeKey(String changeKey) {
        this.changeKey = changeKey;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Event.Source getDomain() {
        return domain;
    }

    public void setDomain(Event.Source domain) {
        this.domain = domain;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<EventAttachment> getHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(List<EventAttachment> hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public String getiCalUId() {
        return iCalUId;
    }

    public void setiCalUId(String iCalUId) {
        this.iCalUId = iCalUId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getIsAllDay() {
        return isAllDay;
    }

    public void setIsAllDay(String isAllDay) {
        this.isAllDay = isAllDay;
    }

    public String getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(String isCancelled) {
        this.isCancelled = isCancelled;
    }

    public Event.Organizer getIsOrganizer() {
        return isOrganizer;
    }

    public void setIsOrganizer(Event.Organizer isOrganizer) {
        this.isOrganizer = isOrganizer;
    }

    public String getIsReminderOn() {
        return isReminderOn;
    }

    public void setIsReminderOn(String isReminderOn) {
        this.isReminderOn = isReminderOn;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOnlineMeetingUrl() {
        return onlineMeetingUrl;
    }

    public void setOnlineMeetingUrl(String onlineMeetingUrl) {
        this.onlineMeetingUrl = onlineMeetingUrl;
    }

    public Event.Organizer getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Event.Organizer organizer) {
        this.organizer = organizer;
    }

    public String getOriginalEndTimeZone() {
        return originalEndTimeZone;
    }

    public void setOriginalEndTimeZone(String originalEndTimeZone) {
        this.originalEndTimeZone = originalEndTimeZone;
    }

    public String getRawBody() {
        return rawBody;
    }

    public void setRawBody(String rawBody) {
        this.rawBody = rawBody;
    }

    public List<String> getRecurrence(List<String> recurrence) {
        return this.recurrence;
    }

    public void setRecurrence(List<String> recurrence) {
        this.recurrence = recurrence;
    }

    public String getResponseRequested() {
        return responseRequested;
    }

    public void setResponseRequested(String responseRequested) {
        this.responseRequested = responseRequested;
    }

    public EventDateTime getStart() {
        return start;
    }

    public void setStart(EventDateTime start) {
        this.start = start;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
