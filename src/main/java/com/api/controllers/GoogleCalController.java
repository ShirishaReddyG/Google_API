package com.api.controllers;

import com.api.AppointmentsObject;
import com.api.ContactsObject;
import com.api.DriveObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GoogleCalController {

	private final static Log logger = LogFactory.getLog(GoogleCalController.class);
	private static final String APPLICATION_NAME = "eSalida";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	GoogleClientSecrets clientSecrets;
	GoogleAuthorizationCodeFlow flow;

	@Value("${google.client.client-id}")
	private String clientId;
	@Value("${google.client.client-secret}")
	private String clientSecret;
	@Value("${google.client.redirectUri}")
	private String redirectURI;

    List<String> SCOPES = Arrays.asList(
            DriveScopes.DRIVE_READONLY,
            PeopleServiceScopes.CONTACTS_READONLY,
            CalendarScopes.CALENDAR_READONLY
    );

	@RequestMapping(value = "/login/google", method = RequestMethod.GET)
	public RedirectView googleConnectionStatus(HttpServletRequest request) throws Exception {
		return new RedirectView(authorize());
	}

	@RequestMapping(value = "/login/google", method = RequestMethod.GET, params = "code")
	public ResponseEntity<String> oauth2Callback(@RequestParam(value = "code") String code) {
		try {
            TokenResponse response = getAccessToken(code);
            long startTime = System.currentTimeMillis();

            PeopleService peopleService = BuildPeopleService(response.getAccessToken());
            List<Person> contacts = getAllContacts(peopleService);
            System.out.println("Contacts: "+contacts);
            List<ContactsObject> contactsData = prepareIndexableContactsObject(contacts);
            ObjectMapper ContactsData = new ObjectMapper();
            ContactsData.writeValue(new java.io.File("ContactsData.json"),contactsData);

            Calendar calendarService = BuildCalendarService(response.getAccessToken());
            List<Event> events = getAppointMentsList(calendarService);
            System.out.println("Appointments : "+events);
            List<AppointmentsObject> appointments = prepareIndexableAppointmentsObject(events);
            ObjectMapper appointmentsData = new ObjectMapper();
            appointmentsData.writeValue(new java.io.File("AppointmentsData.json"),appointments);

            Drive service = BuildDriveService(response.getAccessToken());
            List<File> result = retrieveAllFiles(service);
            System.out.println("Files: "+result);
            List<DriveObject> files = prepreIndexableFileObj(result);
            ObjectMapper DriveData = new ObjectMapper();
            DriveData.writeValue(new java.io.File("DriveData.json"),files);

            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("Response Time: "+totalTime);

        } catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

    public HttpTransport getHttpTransport() {
        HttpTransport httpTransport = null;
        try {
           httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }
        return httpTransport;
    }

    public GoogleAuthorizationCodeFlow fetchAuthorizationCodeFlow(String clientId, String clientSecret) {
        if (flow == null) {
            GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            clientSecrets = new GoogleClientSecrets().setWeb(web);

            flow = new GoogleAuthorizationCodeFlow.Builder(getHttpTransport(), JSON_FACTORY, clientSecrets,
                    SCOPES).setAccessType("offline").setApprovalPrompt("force").build();
        }
        return flow;
    }

	private String authorize() throws Exception {
        GoogleAuthorizationCodeFlow flow = fetchAuthorizationCodeFlow(clientId,clientSecret);
        AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI);
		return authorizationUrl.build();
	}

	public TokenResponse getAccessToken(String code) {
	    TokenResponse reponse = null;
	    try {
            GoogleAuthorizationCodeFlow flow = fetchAuthorizationCodeFlow(clientId, clientSecret);
            reponse = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
        } catch (Exception e) {
            System.out.println("Exception: " +e.getMessage());
        }
        return reponse;
    }

	public Credential getCredential(String AccessToken) {
        return new GoogleCredential().setAccessToken(AccessToken);
    }

    Drive BuildDriveService(String accessToken) throws GeneralSecurityException, IOException {
        Drive service = new Drive.Builder(getHttpTransport(), JacksonFactory.getDefaultInstance(), getCredential(accessToken)).setApplicationName("Example").build();
        return  service;
    }

    public  PeopleService BuildPeopleService(String accessToken) throws IOException {

        return new PeopleService.Builder(getHttpTransport(), JSON_FACTORY, getCredential(accessToken))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public Calendar BuildCalendarService(String accessToken) {
        return new com.google.api.services.calendar.Calendar.Builder(getHttpTransport(), JSON_FACTORY, getCredential(accessToken))
                .setApplicationName(APPLICATION_NAME).build();
    }

	static List<Event> getAppointMentsList(Calendar service) throws IOException {
        List<Event> result = new ArrayList<>();
		Events.List request = service.events().list("primary")
                .setMaxResults(10);
        do {
            try {
                com.google.api.services.calendar.model.Events eventList =
                        request.execute();
                result.addAll(eventList.getItems());
                request.setPageToken(eventList.getNextPageToken());
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
                request.setPageToken(null);
            }
        } while (request.getPageToken() != null &&
                request.getPageToken().length() > 0);
        return result;
	}

    static List<File> retrieveAllFiles(Drive service) throws IOException {
        List<File> result = new ArrayList<File>();
        Drive.Files.List request = service.files().list();
        request.setMaxResults(100);
        do {
            try {
                FileList files = request.execute();
                result.addAll(files.getItems());
//                request.setPageToken(files.getNextPageToken());
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
                request.setPageToken(null);
            }
        } while (request.getPageToken() != null &&
                request.getPageToken().length() > 0);
        return result;
    }

    static List<Person> getAllContacts(PeopleService service) throws Exception{
        List<Person> result = new ArrayList<Person>();
	    PeopleService.People.Connections.List request =
                service.people().connections()
                                .list("people/me")
                                .setPageSize(100);
        do {
            try {
                request.setPersonFields("names,nicknames,imClients,emailAddresses," +
                        "Photos,addresses,locales,birthdays,relations," +
                        "organizations,occupations,phoneNumbers,urls,ageRanges," +
                        "residences,memberships,events,genders,biographies");
                ListConnectionsResponse contacts = request.execute();
                result.addAll(contacts.getConnections());
                request.setPageToken(contacts.getNextPageToken());
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
                request.setPageToken(null);
            }
        } while (request.getPageToken() != null &&
                request.getPageToken().length() > 0);
        return result;
    }
    static List<AppointmentsObject> prepareIndexableAppointmentsObject(List<com.google.api.services.calendar.model.Event> appointments) {
       return appointments.stream().map(appointment ->{
            AppointmentsObject appointmentsData = new AppointmentsObject();
            appointmentsData.setId(appointment.getId());
            appointmentsData.setAttendees(appointment.getAttendees());
            appointmentsData.setiCalUId(appointment.getICalUID());
            appointmentsData.setEnd(appointment.getEtag());
            appointmentsData.setStart(appointment.getStart());
            appointmentsData.setHasAttachments(appointment.getAttachments());
            appointmentsData.setOrganizer(appointment.getOrganizer());
            appointmentsData.setUserId("");
            appointmentsData.setRecurrence(appointment.getRecurrence());
            appointmentsData.setLocation(appointment.getLocation());
            appointmentsData.setDomain(appointment.getSource());
            appointmentsData.setAppointmentBody(appointment.getDescription());
            return appointmentsData;

        }).collect(Collectors.toList());
    }

    static List<DriveObject> prepreIndexableFileObj(List<File> files){
        return files.stream().map(file ->{
            DriveObject fileData = new DriveObject();
            fileData.setcTag("");
            fileData.setFile("");
            fileData.setCreatedBy(file.getOwnerNames());
            fileData.setCreatedDateTime(file.getCreatedDate());
            fileData.seteTag(file.getEtag());
            fileData.setExtension(file.getFileExtension());
            fileData.setFileSystemInfo("");
            fileData.setFolderName(file.getFolderColorRgb());
            fileData.setId(file.getId());
            fileData.setLastModifiedBy(file.getLastModifyingUserName());
            fileData.setLastModifiedDateTime(file.getModifiedDate());
            fileData.setName(file.getTitle());
            fileData.setOwnerName(file.getOwnerNames());
            fileData.setParentReference("");
            fileData.setSize(file.getFileSize());
            fileData.setUserId("1");
            fileData.setWebUrl(file.getAlternateLink());
            return fileData;

        }).collect(Collectors.toList());
    }

   static List<ContactsObject> prepareIndexableContactsObject(List<Person> contacts) {
        return contacts.stream().map(contact ->{
            ContactsObject contactsData = new ContactsObject();
            contact.getNames().stream().forEach(name->{
                contactsData.setFirstName(name.getGivenName());
                contactsData.setDisplayName(name.getDisplayName());
                contactsData.setSurname(name.getFamilyName());
                contactsData.setGivenName(name.getGivenName());
                contactsData.setMiddleName(name.getMiddleName());
            });
            contact.getOrganizations().stream().forEach(organization->{
                contactsData.setCompanyName(organization.getName());
                contactsData.setOrganization(organization.getName());
                contactsData.setDepartment(organization.getDepartment());
            });
            contact.getPhoneNumbers().stream().forEach(data->{
                contactsData.setMobilePhone(data.getCanonicalForm());
            });
            contact.getEmailAddresses().stream().forEach(data->{
                contactsData.setEmailAddress(data.getValue());
            });
            contact.getBirthdays().stream().forEach(data->{
                contactsData.setBirthday(data.getText());
            });
            contact.getAddresses().stream().forEach(Address->{
                contactsData.setCity(Address.getCity());
            });
            return contactsData;
        }).collect(Collectors.toList());
    }
}
//            long startTime = System.currentTimeMillis();
//            long endTime   = System.currentTimeMillis();
//            long totalTime = endTime - startTime;
//            System.out.println("Response Time: "+totalTime);
//            ObjectMapper FileData = new ObjectMapper();
//            FileData.writeValue(new java.io.File("JsonData.txt"),result);
//            files.stream().forEach(file ->{
//
//                System.out.println("filename: "+file.getName());
//            });
