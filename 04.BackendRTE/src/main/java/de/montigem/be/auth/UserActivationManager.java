package de.montigem.be.auth;

import de.montigem.be.MontiGemInitUtils;
import de.montigem.be.auth.jwt.MontiGemSecurityUtils;
import de.montigem.be.config.Config;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.domain.cdmodelhwc.classes.domainuseractivationstatus.DomainUserActivationStatus;
import de.montigem.be.domain.cdmodelhwc.daos.DomainUserDAO;
import de.se_rwth.commons.logging.Log;
import org.apache.shiro.codec.Base64;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

@Stateless
public class UserActivationManager {

  @Inject
  private UserActivationDAO activationDao;

  @Inject
  private DomainUserDAO userDao;

  private Properties props = new Properties();

  private String password;

  private String username;

  private String sender;

  // generate activation key
  private SecureRandom random;

  private String key;

  private String salt;

  private String keyHash;

  private String id;

  public UserActivationManager() throws IOException {
    Properties pwdProps = new Properties();
    InputStream stream = getClass().getClassLoader().getResourceAsStream("mail.properties");
    if (stream != null) { // no mail.properties (should be local)
      pwdProps.load(stream);
      stream.close();
      password = pwdProps.getProperty("pwd");
      username = pwdProps.getProperty("username");
      sender = pwdProps.getProperty("sender");

      props.put("mail.smtp.host", "mail.rwth-aachen.de");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.port", 587);
      props.put("mail.smtp.auth", "true");
      props.put("mail.transport.protocol", "smtp");
    }
  }

  public void sendActivationEmail(String email, String username, String resource,
      String datenbankBezeichner)
      throws MessagingException {
    if (MontiGemInitUtils.isOnServer() && Config.SEND_MAILS) {

      Optional<DomainUser> user = userDao.find(username, resource);
      if (!user.isPresent()) {
        return;
      }

      generateActivationKey(resource, user);

      String link = MontiGemInitUtils.getServerURL() + "/auth/activations?id=" + id + "&key=" + key
          + "&resource=" + resource;
      String mailText = "Sehr geehrter Nutzer,"
          + "<br>"
          + "<br>"
          + "vielen Dank für Ihre Registrierung bei dem Management Cockpit für Lehrstuhl Controlling – MontiGem!"
          + "<br>"
          + "Wir freuen uns, dass Sie sich dafür entschieden haben MontiGem zur Unterstützung des Controllings an Ihrem Lehrstuhl / Institut zu nutzen."
          + "<br>"
          + "<br>"
          + "Um Zugriff auf Ihre MontiGem Instanz <b>"
          + datenbankBezeichner
          + "</b> zu erhalten, klicken Sie bitte den folgenden Bestätigungslink an und setzen ein Passwort: "
          + "<a href='" + link + "'>Hier klicken</a>"
          + "<br>"
          + "<i>MontiGem ist nur aus dem internen RWTH-Netz erreichbar</i>"
          + "<br>"
          + "<br>"
          + "Ihr MontiGem-Benutzerkonto wird automatisch aktiviert und Sie können sich in Zukunft mit Ihrem Benutzernamen sowie dem soeben gesetzten Passwort anmelden."
          + "<br>"
          + "Ihr Benutzername für den Login lautet: <b>" + username + "</b>"
          + "<br>"
          + "<br>"
          + "Über folgenden Link <a href='" + MontiGemInitUtils.getServerURL() + "'>"
          + MontiGemInitUtils.getServerURL() + "</a> gelangen Sie zum MontiGem Login."
          + "<br>"
          + "Dort können Sie sich über Ihren <b>Benutzernamen</b>, <b>Passwort</b> und Ihre <b>Instanz</b> anmelden."
          + "<br>"
          + "Ihre MontiGem Instanz <b>"
          + datenbankBezeichner
          + "</b> können Sie im Auswahlmenü (Dropdown) unterhalb des Passwortfeldes auswählen."
          + "<br>"
          + "<br>"
          + "Bei Rückfragen und Problemen stehen wir Ihnen selbstverständlich gerne zur Verfügung."
          + "<br>"
          + "Weitere Informationen finden Sie zudem im "
          + "<a href='https://MontiGem.rwth-aachen.de/w/index.php/Hauptseite'>Benutzerhandbuch</a>."
          + "<br>"
          + "Da uns Ihre Zufriedenheit sehr am Herzen liegt, freuen wir uns über Anregungen und Feedback."
          + "<br>"
          + "Zögern Sie also nicht uns zu kontaktieren, wenn Sie eine wichtige Funktion vermissen oder Probleme mit der Handhabung haben."
          + "<br>"
          + "<br>"
          + "Herzliche Grüße<br>"
          + "Ihr MontiGem-Team<br>";

      try {
        Log.info("send mail to " + email, getClass().getName());
        sendMail(mailText, email, "[MontiGem] Kontoaktivierung Instanz " + datenbankBezeichner);
        Log.info("successfully sent mail to " + email, getClass().getName());
      } catch (MessagingException e) {
        Log.warn("Exception on send mail: ", e);
      }
    }
  }

  private void generateActivationKey(String resource, Optional<DomainUser> user) {
    // generate activation key
    random = new SecureRandom();
    key = new BigInteger(130, random).toString(32);
    salt = MontiGemSecurityUtils.generateSaltAsBase64();
    keyHash = MontiGemSecurityUtils.encodePassword(key, Base64.decode(salt));
    id = UUID.randomUUID().toString();
    activationDao.addActivation(new UserActivation(id, user.get(), keyHash, salt), resource);
  }

  public void sendForgotPwdMail(String email, String username, String resource,
      String datenbankBezeichner) throws MessagingException {

    Optional<DomainUser> user = userDao.find(username, resource);
    if (!user.isPresent()) {
      return;
    }

    // generate activation key
    generateActivationKey(resource, user);

    String link =
        MontiGemInitUtils.getServerURL() + "/auth/forgotPwd?id=" + id + "&key=" + key + "&resource="
            + resource;
    String mailText = "Sehr geehrter Nutzer der MontiGem Instanz <b>" + datenbankBezeichner + "</b>,"
        + "<br>"
        + "<br>"
        + "um das Passwort für den unter dem Benutzernamen <b>"
        + username
        + "</b> registrierten Nutzer neu zu setzen, klicken Sie bitte auf folgenden Link: "
        + "<a href='" + link + "'>Hier klicken</a>"
        + "<br>"
        + "<br>"
        + "Herzliche Grüße"
        + "<br>"
        + "Ihr MontiGem-Team"
        + "<br>";

    sendMail(mailText, email, "[MontiGem] Passwort Wiederherstellen Instanz " + datenbankBezeichner);
  }

  /**
   * Send emails to a list of recipients with a specific subject and mail body.
   *
   * @param mailText body of the email. Should be of html format.
   * @param emails   list of recipients.
   * @param subject  subject of mail.
   * @throws MessagingException thrown if one of the mails cannot be sent.
   */
  public void sendMail(String mailText, List<String> emails, String subject)
      throws MessagingException {
    for (String email : emails) {
      sendMail(mailText, email, subject);
    }
  }

  /**
   * Send an email to a recipient with a specific subject and mail body.
   *
   * @param mailText body of the email. Should be of html format.
   * @param email    address of recipient.
   * @param subject  subject of mail.
   * @throws MessagingException thrown if the mail cannot be sent.
   */
  public void sendMail(String mailText, String email, String subject) throws MessagingException {
    if (sender != null) {
      Session session = Session.getInstance(props, new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(username, password);
        }
      });

      // create mail
      final MimeMessage mail = new MimeMessage(session);
      mail.setFrom(new InternetAddress("MontiGem" + "<" + sender + ">"));
      final InternetAddress[] address = { new InternetAddress(email) };
      mail.setRecipients(MimeMessage.RecipientType.TO, address);
      mail.setSubject(subject);
      mail.setSentDate(new Date());
      mail.setText(mailText, "UTF-8", "html");

      Transport t = session.getTransport();
      t.connect();
      t.sendMessage(mail, mail.getAllRecipients());
    } else {
      Log.debug("sending mails is not possible, no given sender", getClass().getName());
    }
  }

  public Optional<DomainUser> activateAccount(UUID activationId, String activationKey,
      String resource) {
    UserActivation activation = activationDao
        .checkActivationKey(activationId, activationKey, resource);
    if (activation == null) {
      return Optional.empty();
    }

    // activate user and remove activation key from database
    activation.getUser().setActivated(DomainUserActivationStatus.AKTIVIERT);
    userDao.update(activation.getUser(), resource);
    Optional<DomainUser> res = Optional.of(activation.getUser());
    // activationDao.deleteActivationKey(activationId);
    return res;
  }
}

